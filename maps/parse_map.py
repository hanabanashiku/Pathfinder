import sys
import numpy as np
import cv2
from matplotlib import pyplot as plt
import pandas as pd
import pytesseract as pct
from pytesseract import Output

BINARY_THRESH = 127
MAP_HEIGHT = 1000


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
    data = pct.image_to_data(img, lang=lang, output_type=Output.DATAFRAME)
    out = []
    for _, row in data.iterrows():
        # filter out garbage
        text = str(row['text'])
        if text.isspace() or text == 'nan':
            continue

        w = row['width']
        h = row['height']
        a = (row['left'], row['top'])
        b = (a[0] + w, a[1])
        c = (a[0], a[1] + h)
        d = (b[0], c[1])

        out.append([a, c, b, d, text])

    return pd.DataFrame(out, columns=['top_left', 'bottom_left', 'top_right', 'bottom_right', 'text'])


# try to find edges based on feature nodes
# provide an array of 2D points and an image
#def find_edges(nodes, img):


# main entry point
if __name__ == '__main__':
    args = sys.argv

    if len(args) == 1:
        print('This script is used for parsing a map blueprint for storage into a database.')
        print('Dependencies: python3, numpy, scikit-image')
        print('To run the script, simply run python3 [path/to/image]')
        exit(1)

    if len(args) != 2:
        print('Invalid number of arguments', file=sys.stderr)
        exit(1)

    image = get_image(args[1])
    print(get_text(image))
    corners = cv2.goodFeaturesToTrack(image, 3000, 0.06, 15, blockSize=5)
    for i in range(len(corners)):
        image = cv2.circle(image, (corners[i][0][0], corners[i][0][1]), 5, 150, 2)
    show_image(image)
