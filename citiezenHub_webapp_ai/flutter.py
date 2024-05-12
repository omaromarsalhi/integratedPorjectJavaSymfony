import requests
from PIL import Image



def downloadImage(images_url):

    # file_ext = images_url.split('.')[-1]
    # file_name = images_url.split('.')[-2].split('/')[-1]

    # Save the image with its original name and extension
    # Get the image content
    data = requests.get(images_url).content
    # Save the data to a file named "img.jpg"
    with open("images/omar.png", "wb") as f:
        f.write(data)




