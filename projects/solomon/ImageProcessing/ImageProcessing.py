import cv2
import random
import matplotlib.pyplot as plt

#get the image from the memory and convert to grayscale
path = r"C:\Users\beia\Desktop\StoreMaps\map6.jpg"
try:
    originalImage = cv2.imread(path)
    originalImage = cv2.cvtColor(originalImage, cv2.COLOR_BGR2RGB)
    grayScaleImage = cv2.cvtColor(originalImage, cv2.COLOR_BGR2GRAY)
    resultImage = cv2.cvtColor(grayScaleImage, cv2.COLOR_GRAY2RGB)
except IOError:
    pass

#mean blur - for testing purposes
meanBluredImage = cv2.blur(grayScaleImage, (9, 9))
#gaussian blur - for testing purposes
gaussianBluredImage = cv2.GaussianBlur(grayScaleImage,(3,3),0)

#apply sobel operator - for testing purpose
sobelx = cv2.Sobel(gaussianBluredImage,cv2.CV_64F,1,0,ksize=3)
sobely = cv2.Sobel(gaussianBluredImage,cv2.CV_64F,0,1,ksize=3)

#apply canny algorithm which includes all the previos steps
edges = cv2.Canny(gaussianBluredImage, 50, 300, 19)
#find contours
contours, hierarchy = cv2.findContours(edges,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
for contourNr in range(len(contours)):
    cv2.drawContours(resultImage, contours, contourNr, (random.randint(0, 255),random.randint(0, 255),random.randint(0, 255)), 2)


#show processed photo near the original grayscale photo
plt.figure(1)
plt.subplot(1, 2, 1)
plt.title("Grayscale image")
plt.imshow(grayScaleImage, cmap="gray", vmin=0, vmax=255)
plt.subplot(1, 2, 2)
plt.title("Edges")
plt.imshow(resultImage, cmap="gray", vmin=0, vmax=255)
plt.show()