import sys
from parse_map import *

args = sys.argv
image = get_image(args[1])
nodes = get_nodes(image)
edges = find_edges(nodes, image)
doors = get_doors(image)
text = get_text(image)
for n in nodes:
    image = cv2.circle(image, (n[0], n[1]), 5, 150, 1)
for _, row in text.iterrows():
    image = cv2.rectangle(image, row['top_left'], row['bottom_right'], (0, 0, 255), 2)
for _, row in doors.iterrows():
    image = cv2.rectangle(image, row['top_left'], row['bottom_right'], (0, 255, 255), 2)
print(edges)
show_image(image)
