import cv2
import random
import matplotlib.pyplot as plt
import numpy as np

#get the image from the memory and convert to grayscale
path = r"C:\Users\beia\Desktop\StoreMaps\map6.jpg"
try:
    originalImage = cv2.imread(path)
    originalImage = cv2.cvtColor(originalImage, cv2.COLOR_BGR2RGB)
    grayScaleImage = cv2.cvtColor(originalImage, cv2.COLOR_BGR2GRAY)
    resultImage = cv2.cvtColor(grayScaleImage, cv2.COLOR_GRAY2RGB)
except IOError:
    pass

#remove the black pixel from the image and replace them with the backround
'''
for i in range(grayScaleImage.shape[0]):
    for j in range(grayScaleImage.shape[1]):
        if originalImage[i][j] == 0:
'''


#mean blur - for testing purposes
meanBluredImage = cv2.blur(grayScaleImage, (3, 3))
#gaussian blur - for testing purposes
gaussianBluredImage = cv2.GaussianBlur(grayScaleImage,(3,3),0)

#apply sobel operator - for testing purpose
sobelx = cv2.Sobel(gaussianBluredImage,cv2.CV_64F,1,0,ksize=3)
sobely = cv2.Sobel(gaussianBluredImage,cv2.CV_64F,0,1,ksize=3)

#apply canny algorithm which includes all the previos steps
edges = cv2.Canny(gaussianBluredImage, 0, 200, 3)

#find contours
contours, hierarchy = cv2.findContours(edges,cv2.RETR_TREE,cv2.CHAIN_APPROX_NONE)


#get the bounding rectangles areas of the contours and insert it into an array of tuples tha contain the contour and the rectangle bounding area
boundingRectanglesAreas = []
for contour in contours:
    rect = cv2.minAreaRect(contour)
    points = cv2.boxPoints(rect)
    points = np.int0(points)
    area = np.sqrt((points[0][0] - points[1][0]) ** 2 + (points[0][1] - points[1][1]) ** 2) * np.sqrt((points[1][0] - points[2][0]) ** 2 + (points[1][1] - points[2][1]) ** 2)
    boundingRectanglesAreas.append((contour, area))

#find the maximum rectangle bounding area of the contours and create a threshold
boundingAreaExclusionThreshold = np.max(np.array([boundingRectanglesArea[1] for boundingRectanglesArea in boundingRectanglesAreas])) / 2000

#get only the contours which bounding rectangles have a area smaller than a certain threshold so that we can eliminate the text from the image
#check if the solidity of the contour is big enough(checking the convexity of the contour by computing the ratio of the contour area to the bounding region area)
contoursWithoutText = []
for boundingRectangleArea in boundingRectanglesAreas:
    if ~cv2.isContourConvex(boundingRectangleArea[0]) and boundingRectangleArea[1] > boundingAreaExclusionThreshold:
        contoursWithoutText.append(boundingRectangleArea[0])

#draw contours
for contourNr in range(len(contoursWithoutText)):
    cv2.drawContours(resultImage, contoursWithoutText, contourNr, (random.randint(0, 255),random.randint(0, 255),random.randint(0, 255)), 2)

#show processed photo near the original grayscale photo
plt.figure(1)
plt.subplot(1, 2, 1)
plt.title("Edges")
plt.imshow(edges, cmap="gray", vmin=0, vmax=255)
plt.subplot(1, 2, 2)
plt.title("Contours")
plt.imshow(resultImage, cmap="gray", vmin=0, vmax=255)
plt.show()