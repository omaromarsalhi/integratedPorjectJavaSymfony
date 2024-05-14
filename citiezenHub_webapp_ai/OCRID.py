import json
from pathlib import Path
import requests

def idRcognition(path,fileName):
    headers = {"Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNWI4ZWIwMmEtMGQ0OC00NjEwLTkxY2YtOTM1Yjk5NTk4YjA2IiwidHlwZSI6ImFwaV90b2tlbiJ9.YUcWSftTzeFrz1p_tJxVa-4kzhNuv47XTL4j5M9bVzU"}

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
