import os
import re
suffix = '.java'  #指定后缀名

def main():
    #findRegex('cancle')
    lineCount()
def lineCount():
    path = os.getcwd()
    words = {}
    count = 0
    for root, dirs, files in os.walk(path):
        for file in files:
            if file[-len(suffix):] == suffix :
                # 统计行数
                f = open(root + '\\' + file,'r',encoding = 'utf-8')
                line = f.readline()
                while(line):
                    line = f.readline()
                    count += 1
                f.close()
    print("line count = " + str(count))
   
def findRegex(regex):
    path = os.getcwd()
    words = {}
    count = 0
    for root, dirs, files in os.walk(path):
        for file in files:
            if file[-len(suffix):] == suffix :
                # 查找内容
                f = open(root + '\\' + file,'r',encoding = 'utf-8')
                text = f.read()
                f.close()
                find = re.findall(regex, text,flags=re.S)
                if find:
                    print(file)
                    print(find)
                
def wordCoun():
    path = os.getcwd()
    words = {}
    count = 0
    for root, dirs, files in os.walk(path):
        for file in files:
            if file[-len(suffix):] == suffix :
                # 单词计数
                f = open(root + '\\' + file,'r',encoding = 'utf-8')
                text = f.read()
                f.close()
                splits = re.findall('(\w+)\W',text)
                for word in splits:
                    if word not in words.keys():
                        words.setdefault(word,1)
                    else:
                        words[word] += 1
    for key,value in sorted(words.items()):
        print(key,'  :  ',value)
        
if __name__=="__main__" :
    main()
