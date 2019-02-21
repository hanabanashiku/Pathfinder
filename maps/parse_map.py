import sys
import os
import numpy as np
import cv2
import pandas as pd
import pytesseract as pct

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
    doors = []
    # filter through each type of door
    for i in os.listdir('ocr_img/doors'):
        template = cv2.imread('ocr_img/doors/' + i, 0)
        door_type = DoorTypes.get_door_type(i)
        w, h = template.shape[::-1]
        res = cv2.matchTemplate(img, template, cv2.TM_CCOEFF_NORMED)
        loc = np.where(res >= TEMP_THRESH)
        for j in zip(*loc[::-1]):
            doors.append(get_box(j, h, w) + [door_type])
    return pd.DataFrame(doors, columns=['top_left', 'bottom_left', 'top_right', 'bottom_right', 'door_type'])

# try to find edges based on feature nodes
# provide an array of 2D points and an image
# def find_edges(nodes, img):


# main entry point
if __name__ == '__main__':
    args = sys.argv

    if len(args) == 1:
        print('This script is used for parsing a map blueprint for storage into a database.')
        print('Dependencies: python3, numpy, pandas, pytesseract, cv2')
        print('To run the script, simply run python3 [path/to/image]')
        exit(1)

    if len(args) != 2:
        print('Invalid number of arguments', file=sys.stderr)
        exit(1)

    image = get_image(args[1])
    corners = cv2.goodFeaturesToTrack(image, 3000, 0.06, 15, blockSize=5)
    doors = get_doors(image)
    text = get_text(image)
    for c in range(len(corners)):
        image = cv2.circle(image, (corners[c][0][0], corners[c][0][1]), 5, 150, 1)
    for _, row in text.iterrows():
        image = cv2.rectangle(image, row['top_left'], row['bottom_right'], (0, 0, 255), 2)
    for _, row in doors.iterrows():
        image = cv2.rectangle(image, row['top_left'], row['bottom_right'], (0, 255, 255), 2)
    show_image(image)
