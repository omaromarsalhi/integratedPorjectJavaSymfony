import json
import requests
import subprocess


def execute_cmd(command):
    try:
        result = subprocess.run(command, shell=True, capture_output=True, text=True)
        if result.returncode == 0:
            return result.stdout
    except Exception as e:
        print(f"Error executing command: {e}")


def generateBasedOnText(prompt,prompt2,url):
    r = requests.post(url+'/api/generate',
                    json={
                        'model': 'mistral',
                        'prompt': 'is this paragraph "'+prompt+'" speaks or describe even in a vage way about   "'+prompt2+'"  , please answer with yes or no and a very short explanation' ,
                    },
                    stream=True)
    r.raise_for_status()
    return r.content.decode('utf-8');


def generateBasedOnCategory(prompt,prompt2,url):
    r = requests.post(url+'/api/generate',
                    json={
                        'model': 'mistral',
                        'prompt': 'is the main objext in this paragraph "'+prompt+'" falls under this category "'+prompt2+'" , please answer with yes or no and a very short explanation ',
                    },
                    stream=True)
    r.raise_for_status()
    return r.content.decode('utf-8');

def lookFroSemilarData(prompt,prompt2,url):
    r = requests.post(url+'/api/generate',
                    json={
                        'model': 'mistral',
                        'prompt': 'does this paragraph "'+prompt+'" looks similar or describe the same thing or object in general or falls under the category  compared to this one "'+prompt2+'" , please answer with yes or no ',
                    },
                    stream=True)
    r.raise_for_status()
    return r.content.decode('utf-8');




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

# print(tuned_response)
# generateBasedOnText(tuned_response,"Golf")
