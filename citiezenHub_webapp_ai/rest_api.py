from flask import Flask,request,jsonify
from langchain_helper import generate_produxt_description
# import ollama_vesion as ov
# import OCRID
import flutter as ft
import subprocess
import json
import requests
from PIL import Image



app = Flask(__name__)

url='https://2b3e-34-29-90-192.ngrok-free.app'


def finetune_text(text):
    tuned_response=''
    for string in text.split('\n'):
        if string.find("failed")==-1:
            tuned_response+=string
    return tuned_response


def finetune_resp(text):
    newResp=text.split('\n')
    del newResp[-1]
    final_response=''
    for i in newResp:
        body = json.loads(i)
        final_response+=body.get('response', '')
    return final_response

def testFlutterFlow(image_url):
        command=f"set OLLAMA_HOST={url}&ollama run llava describe the image content only {image_url}"
        result = subprocess.run(command, shell=True, capture_output=True, text=True)
        tuned_response=finetune_text(result.stdout)
        return tuned_response
def generateBasedOnText(prompt,prompt2,url):
    r = requests.post(url+'/api/generate',
                    json={
                        'model': 'mistral',
                        'prompt': 'is this paragraph "'+prompt+'" speaks about "'+prompt2+'" , please answer with yes or no',
                    },
                    stream=True)
    r.raise_for_status()
    return r.content.decode('utf-8');


def downloadImage(images_url):
    data = requests.get(images_url).content
    # Save the data to a file named "img.jpg"
    with open("images/omar.png", "wb") as f:
        f.write(data)

def generateBasedOnCategory(prompt,url):
    r = requests.post(url+'/api/generate',
                    json={
                        'model': 'mistral',
                        'prompt':  """
                            I have a """+prompt+""" witch is a product's title for sale.
                            give me a descreption that i can put on that product to get peaple to buy it.
                        """,
                    },
                    stream=True)
    r.raise_for_status()
    return r.content.decode('utf-8');

@app.route('/flutterflow/test',methods=['POST'])
def generateFlutterFlow():
    image_url=request.form['image_url']
    title=request.form['title']
    print("/////////")
    print(title)
    print("/////////")
    ft.downloadImage(image_url)
    apirep=testFlutterFlow('C:\\Users\\omar salhi\\Desktop\\PIDEV\\citiezenHub_webapp_ai\\images\\omar.png')
    text=generateBasedOnText(apirep,title,url)
    tuned_response=finetune_resp(text)
    new_string = tuned_response.replace("paragraph", "image")


    if new_string.lower().find("yes") != -1:
        obj={
            'rep':'great'
        }
        return jsonify(obj),200
    elif new_string.lower().find("no") != -1:
        obj={
            'rep':new_string
        }
        return jsonify(obj),400


@app.route('/flutterflow/generate',methods=['POST'])
def generateFlutterFlowGenerate():
    title=request.form['title']
    text=generateBasedOnCategory(title,url)
    tuned_response=finetune_resp(text)
    obj={
        'rep':tuned_response
    }
    return jsonify(tuned_response),200






# @app.route('/get-descreption',methods=['POST'])
# def generate():
#     text=generate_produxt_description(request.args.get('title'))
#     return jsonify(text),200

# @app.route('/get-product_image_descreption',methods=['POST'])
# def generate_desc_image():
#     image_url=request.args.get('image_url')
#     commands=f"set OLLAMA_HOST={url}&ollama run llava describe the image content only {image_url}"
#     text=ov.execute_cmd(commands)
#     tuned_response=ov.finetune_text(text)
#     return jsonify(tuned_response),200


# @app.route('/get-title_validation',methods=['POST'])
# def generate_title_validation():
#     desc=request.args.get('desc')
#     title=request.args.get('title')
#     text=ov.generateBasedOnText(desc,title,url)
#     tuned_response=ov.finetune_resp(text)
#     return jsonify(tuned_response),200

# @app.route('/get-category_validation',methods=['POST'])
# def generate_category_validation():
#     desc=request.args.get('desc')
#     title=request.args.get('category')
#     text=ov.generateBasedOnCategory(desc,title,url)
#     tuned_response=ov.finetune_resp(text)
#     return jsonify(tuned_response),200


# @app.route('/get-OCR_result',methods=['POST'])
# def generate_OCR_result():
#     path=str(request.args.get('path'))
#     fileName=request.args.get('fileName')
#     OCRID.idRcognition(path,fileName)
#     return jsonify('done'),200


if __name__== '__main__':
    app.run()
