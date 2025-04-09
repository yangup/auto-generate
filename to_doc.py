from yplib import *

file_list = get_file(r'D:\github\auto-generate\.auto\txt')

cat_number = 0
for file in file_list:
    cat_number += 1
    # file_data = to_list_from_txt(file, sep_line_contain='-----------------------------------------------------------------------------------------------------------')
    file_data = open(file, 'r', encoding='utf-8').readlines()
    file_data_list = []
    temp_list = []
    for line in file_data:
        if line.startswith('-----------------------------------------------------------------------------------------------------------'):
            file_data_list.append(temp_list)
            temp_list = []
        else:
            temp_list.append(line)

    page_number = 0
    for data in file_data_list:
        page_number += 1

        name = list(filter(lambda x: str(x).startswith('- 接口概述 : '), data))[0].replace('- 接口概述 : ', '')[3:]

        page_content = ''.join(data)
        data = {
            "api_key": '15bfcd6c08603b253a53e1c71b0d2a1c156841',
            "api_token": '6fdb2612295a8f324c7cdcb599f1c57108188',
            # "cat_name": f"{cat_number}-{name}",
            "page_title": f"{cat_number}.{page_number} {name}",
            "page_content": page_content,
            "s_number": str(cat_number)
        }

        response = requests.post('https://www.showdoc.cc/server/api/item/updateByApi', data=data)

        if response.status_code == 200:
            try:
                result = response.json()
                if str(result.get("error_code", "")) != "0":
                    print(f"show doc error : {json.dumps(result, ensure_ascii=False)}")
                else:
                    print(f"show doc success : {name}")
            except json.JSONDecodeError:
                print("Failed to parse response JSON.")
        else:
            print(f"HTTP request failed with status code {response.status_code}")

print('end_end')
