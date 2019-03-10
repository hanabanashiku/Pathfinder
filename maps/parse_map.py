import sys
import os
import timeit
import numpy as np
import cv2
import pandas as pd
import pytesseract as pct
import json

BINARY_THRESH = 127
TEMP_THRESH = 0.8
MAP_HEIGHT = 1000


class DoorTypes:
    INTERIOR = 0,
    EXTERIOR = 1,
    POCKET = 2,
    BIFOLD = 4,
    SLIDING = 5,
    CASED = 6

    @staticmethod
    def get_door_type(door):
        fname = os.path.splitext(door)[0]
        if fname == 'interior':
            return DoorTypes.INTERIOR
        if fname == 'exterior':
            return DoorTypes.EXTERIOR
        if fname == 'pocket':
            return DoorTypes.POCKET
        if fname == 'cased':
            return DoorTypes.CASED
        if fname == 'bifold':
            return DoorTypes.BIFOLD
        if fname == 'sliding':
            return DoorTypes.SLIDING
        return False


def pt_is_equal(a, b):
    return a[0] == b[0] and a[1] == b[1]


def pt_truth_iterator(points, pt):
    return [pt_is_equal(pt, x) for x in points]


def edge_truth_iterator(edges, edge):
    return [(pt_is_equal(edge[0], x[0]) and pt_is_equal(edge[1], x[1]))
            or (pt_is_equal(edge[1], x[0]) and pt_is_equal(edge[0], x[1])) for x in edges]


# get the raw image as a gray-scale np array
def get_image(path):

    img = cv2.imread(path, cv2.IMREAD_GRAYSCALE)

    # make the image black and white (binary)
    img = cv2.threshold(img, BINARY_THRESH, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)[1]

    # scale down to 800 height
    scale = MAP_HEIGHT / img.shape[0]
    if scale < 1:
        h, w = [int(x * scale) for x in img.shape]
        img = cv2.resize(img, (w, h), interpolation=cv2.INTER_AREA)
    return img


def show_image(img, title='image'):
    cv2.imshow(title, img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


def get_nodes(img):
    nodes = []
    features = cv2.goodFeaturesToTrack(img, 3000, 0.06, 15, blockSize=5)
    for c in range(len(features)):
        nodes.append((features[c][0][0], features[c][0][1]))
    return np.array(nodes)


# takes a 2D point tuple of the top left corner, a height, and a width
# returns a box array with top_left, bottom_left, top_right, bottom_right
def get_box(pt, h, w):
    return [pt, (pt[0], pt[1] + h), (pt[0] + w, pt[1]), (pt[0] + w, pt[1] + h)]


# is a 2D shape contained entirely within another shape?
def is_inside_of(container, box):
    min_x = np.amin(container[:, 0])
    max_x = np.amax(container[:, 0])
    min_y = np.amin(container[:, 1])
    max_y = np.amax(container[:, 1])

    for i in box:
        if i[0] < min_x or i[0] > max_x:
            return False
        if i[1] < min_y or i[1] > max_y:
            return False
    return True


# returns a pandas DataFrame containing the four coordinates making up the bounding box of all text, and the text itself
def get_text(img, lang='eng'):
    data = pct.image_to_data(img, lang=lang, output_type=pct.Output.DATAFRAME)
    out = []
    for _, row in data.iterrows():
        # filter out garbage
        text = str(row['text'])
        if text.isspace() or text == 'nan':
            continue

        w = row['width']
        h = row['height']
        a = (row['left'], row['top'])
        out.append(get_box(a, h, w) + [text])

    return pd.DataFrame(out, columns=['top_left', 'bottom_left', 'top_right', 'bottom_right', 'text'])


# returns a DataFrame containing a list of doors in the map and their type.
def get_doors(img):
    ret = []
    # filter through each type of door
    for i in os.listdir('ocr_img/doors'):
        template = cv2.imread('ocr_img/doors/' + i, 0)
        door_type = DoorTypes.get_door_type(i)
        w, h = template.shape[::-1]
        res = cv2.matchTemplate(img, template, cv2.TM_CCOEFF_NORMED)
        loc = np.where(res >= TEMP_THRESH)
        for j in zip(*loc[::-1]):
            ret.append(get_box(j, h, w) + [door_type])
    return pd.DataFrame(ret, columns=['top_left', 'bottom_left', 'top_right', 'bottom_right', 'door_type'])


# get all neighbors of a 2D point.
def get_point_neighbors(img, pt):
    pt = (int(pt[0]), int(pt[1]))
    shape = img.shape
    neighbors = []
    for i in range(pt[0] - 1, pt[0] + 2):
        for j in range(pt[1] - 1, pt[1] + 2):
            if (i != pt[0] and j != pt[1]) and i < shape[0] and j < shape[0] and i >= 0 and j >= 0:
                neighbors.append((i, j))
    return np.array(neighbors)


# find the other end of an edge given a node.
# takes an image, a set of nodes, a current node, a reference to a list of visited nodes
def crawl_edge(img, nodes, node, visited):
    for pt in get_point_neighbors(img, node):
        # look non-blank pixels
        if img[pt[1]][pt[0]] != 255:
            continue
        if not any(pt_truth_iterator(visited, pt)):
            if any(pt_truth_iterator(nodes, pt)):
                return pt
            visited.append(pt)
            return crawl_edge(img, nodes, pt, visited)
    return False  # there is no other edge


# find an edge given a node.
def find_edge(img, nodes, node):
    visited = [node]
    b = crawl_edge(img, nodes, node, visited)
    if b is not False:
        return node, b
    return False


# try to find edges based on feature nodes
# provide an array of 2D points and an image
def find_edges(nodes, img):
    edges = []
    for i in nodes:
        edge = find_edge(img, nodes, i)
        if edge is not False and not any(edge_truth_iterator(edges, edge)):
            edges.append(edge)
    return edges


current_i = 0
index = {}
S = []
low_link = {}


# Tarjan's strongly connected components algorithm
# returns a list of components
def find_rooms(V, E):
    find_rooms.index = {}
    result = []
    for v in V:
        if v not in index:
            for j in strong_connect(v, E):
                result.append(j)
    return result


def strong_connect(v, E):
    global current_i, low_link, index, S
    index[v] = current_i
    result = []
    low_link[v] = current_i
    current_i += 1
    S.append(v)

    # considering successors of v
    for j in E:
        if j[0] == v:
            w = j[1]
        elif j[1] == v:
            w = j[0]
        else:
            continue

        if w not in index:
            for k in strong_connect(w, E):
                result.append(k)
            low_link[v] = min(low_link[v], low_link[w])

        elif w in S:
            low_link[v] = min(low_link[v], low_link[w])

    if low_link[v] == index[v]:
        w = None
        component = []
        while w is not v:
            w = S.pop()
            component.append(w)
        result.append(component)
    return result


# main entry point
if __name__ == '__main__':
    args = sys.argv

    if len(args) == 1:
        print('This script is used for parsing a map blueprint for storage into a database.')
        print('Dependencies: python3, numpy, pandas, pytesseract, cv2')
        print('To run the script, simply run python3 [path/to/image]')
        exit(1)

    if len(args) == 3:
        floor = args[2]
    elif len(args == 2):
        floor = 1
    else:
        floor = 0  # silence an error message
        print('Invalid number of arguments', file=sys.stderr)
        exit(1)

    start = timeit.default_timer()
    image = get_image(args[1])
    nodes = get_nodes(image)
    edges = find_edges(nodes, image)
    rooms = find_rooms(nodes, edges)
    doors = get_doors(image)
    text = get_text(image)

    jsn = {'nodes': [], 'edges': [], 'rooms': [], 'floor_connectors': [], 'name': None, 'timer': None}

    for i in nodes:
        jsn['nodes'].append((i[0], floor, i[1]))

    for i in edges:
        edge = {
            0: (i[0][0], floor, i[0][1]),
            1: (i[1][0], floor, i[1][1])
        }
        jsn['edges'].append(edge)

    for i in rooms:
        room = {}
        r_nodes = []
        for j in i:
            r_nodes.append((j[0], floor, j[1]))
        room['nodes'] = r_nodes

        r_doors = []
        for _, r in doors.iterrows():
            box = [r['top_left'], r['bottom_left'], r['top_right'], r['bottom_right']]
            if is_inside_of(i, box):
                door = {
                    'top_left': (r['top_left'][0], floor, r['top_left'][1]),
                    'bottom_left': (r['bottom_left'][0], floor, r['bottom_left'][1]),
                    'top_right': (r['top_right'][0], floor, r['top_right'][1]),
                    'bottom_right': (r['bottom_right'][0], floor, r['bottom_right'][1]),
                    'type': r['door_type']
                }
                r_doors.append(door)

        r_text = []
        for _, r in text.iterrows():
            box = [r['top_left'], r['bottom_left'], r['top_right'], r['bottom_right']]
            if is_inside_of(i, box):
                r_text.append(r['text'])
        room['name'] = ' '.join(r_text)
        room['timer'] = timeit.default_timer() - start
        jsn['rooms'].append(room)
    print(json.dumps(jsn))
