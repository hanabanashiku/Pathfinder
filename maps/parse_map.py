import sys
import numpy as np
from skimage import io
from skimage.feature import corner_harris, corner_peaks
from skimage import filters as fl
from skimage.color import rgb2gray
from matplotlib import pyplot as plt

# get the raw image as a gray-scale np array
def get_image(path):
    img = io.imread(path)
    # make the image black and white
    img = np.where(img > np.mean(img), 1.0, 0.0)
    return img


def get_corners(img):
    corners = corner_peaks(corner_harris(img),min_distance=2)
    return zip(*corners)


def show_corners(img, corners):
    x, y = corners
    fig = plt.figure()
    plt.imshow(img)
    plt.plot(x, y, 'o')
    plt.xlim(0, img.shape[1])
    plt.ylim(img.shape[0], 0)
    fig.set_size_inches(np.array(fig.get_size_inches()) * 1.5)
    plt.show()
    print(str(len(corners)) + ' corners')


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