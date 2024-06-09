import json
from pathlib import Path
import requests

def idRcognition(path,fileName):
    headers = {"Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiOGU5NWNkM2QtMGE1ZS00Nzk0LWIxMjQtNTE4ZDIyMGQ5YWRlIiwidHlwZSI6ImFwaV90b2tlbiJ9.wa3nQfnDfuFCE_mcY5FxhOwIBXAJ52itAJ45lhru35c"}

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
