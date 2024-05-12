import json
from pathlib import Path
import requests

def idRcognition(path,fileName):
    headers = {"Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMGYxN2RiZmEtY2YyNC00N2U3LWIzYTAtZmQzNTA3ODRlNzI4IiwidHlwZSI6ImFwaV90b2tlbiJ9.CwuUK4YxXSzyCehgbSt1clNsHMC_4Wgu-4sHRKhV2vo"}

    url = "https://api.edenai.run/v2/ocr/ocr"
    data = {
        "providers": "google",
        "language": "ar",
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
