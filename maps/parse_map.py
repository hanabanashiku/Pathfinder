import sys
import numpy as np
import cv2
from skimage import io
from skimage.feature import corner_harris, corner_peaks
from skimage.filters import threshold_mean, gaussian as gaus
from skimage.color import rgb2gray
from matplotlib import pyplot as plt

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

    corners = cv2.goodFeaturesToTrack(image, 5000, 0.06, 25)
    for i in range(len(corners)):
        image = cv2.circle(image, (corners[i][0][0], corners[i][0][1]), 5, 150, 2)
    show_image(image)
