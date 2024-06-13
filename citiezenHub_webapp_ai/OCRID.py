import json
from pathlib import Path
import requests

def idRcognition(path,fileName):
    headers = {"Authorization": "Bearer"}

    url = "https://api.edenai.run/v2/ocr/ocr"
    data = {
        "providers": "google",
        "language": "en",
    }

    payload = {
        "response_as_dict": True,
        "attributes_as_list": False,
        "show_original_response": True
    }


    files = {"file": open(path, 'rb')}

    response = requests.post(url, data=data,json=payload, files=files, headers=headers)

    result = json.loads(response.text)

    with open('../files/usersJsonFiles/'+fileName+'.json', 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False)
