from flask import Flask,request,jsonify
import ollama_vesion as ov
import OCRID



app = Flask(__name__)

url=''
model='llava:13b'



@app.route('/get-descreptionJava', methods=['POST'])
def generateJava():
    data = request.get_json()
    title = data.get('title', '')
    text=ov.generate_produxt_description(title,url)
    tuned_response=ov.finetune_resp(text)
    return jsonify(tuned_response),200

@app.route('/get-descreption',methods=['POST'])
def generate():
    text=ov.generate_produxt_description(request.args.get('title'),url)
    tuned_response=ov.finetune_resp(text)
    return jsonify(tuned_response),200


@app.route('/get-product_image_descreption',methods=['POST'])
def generate_desc_image():
    image_url=request.args.get('image_url')
    print("image url : "+image_url)
    commands=f"set OLLAMA_HOST={url}&ollama run {model} describe this image {image_url}"
    text=ov.execute_cmd(commands)
    tuned_response=ov.finetune_text(text)
    return jsonify(tuned_response),200



@app.route('/get-product_image_descreption_title',methods=['POST'])
def generate_title_image():
    image_url=request.args.get('image_url')
    title=request.args.get('title')
    print("image url : "+image_url)
    commands=f"set OLLAMA_HOST={url}&ollama run {model}  is this is an {image_url} image of {title} ? "
    text=ov.execute_cmd(commands)
    tuned_response=ov.finetune_text(text)
    return jsonify(tuned_response),200


@app.route('/get-title_validation',methods=['POST'])
def generate_title_validation():
    desc=request.args.get('desc')
    title=request.args.get('title')
    text=ov.generateBasedOnText(desc,title,url)
    tuned_response=ov.finetune_resp(text)
    return jsonify(tuned_response),200

@app.route('/get-category_validation',methods=['POST'])
def generate_category_validation():
    desc=request.args.get('desc')
    title=request.args.get('category')
    text=ov.generateBasedOnCategory(desc,title,url)
    tuned_response=ov.finetune_resp(text)
    return jsonify(tuned_response),200


@app.route('/looksForsemilairity',methods=['POST'])
def look():
    p1=request.args.get('p1')
    p2=request.args.get('p2')
    text=ov.lookFroSemilarData(p1,p2,url)
    tuned_response=ov.finetune_resp(text)
    return jsonify(tuned_response),200


@app.route('/get-OCR_result',methods=['POST'])
def generate_OCR_result():
    path=str(request.args.get('path'))
    fileName=request.args.get('fileName')
    OCRID.idRcognition(path,fileName)
    return jsonify('done'),200


if __name__== '__main__':
    app.run()
