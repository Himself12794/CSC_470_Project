'''
Created on Sep 17, 2015

@author: Philip
'''
    
def get_mean(data):
    return sum(data) / len(data)

def get_median(data):
    length = len(data)
    insertion_sort(data)
            
    if length % 2 == 1:
        return data[length - 1]
    else:
        return (data[int(length / 2)] + data[int(length / 2) - 1]) / 2
    
def get_mode(data):
    counts = {}
    
    maximumCount = None
    maximumValue = None
    
    for datum in data:
        if not datum in counts:
            counts[datum] = 1
        else:
            counts[datum] += 1
        
        maximumCount = counts[datum]
        maximumValue = datum
    
    for value, count in counts.items():
        if count > maximumCount: 
            maximumCount = count
            maximumValue = value
    
    return maximumValue

def get_standard_deviation(data, pop = True):
    average = get_mean(data)
    return (sum([(x - average) ** 2 for x in data]) / (len(data) - (0 if pop else 1))) ** 0.5        

def insertion_sort(A):
    """Sort list of items in ascending order"""
    for k in range(1, len(A)):
        cur = A[k]
        j = k
        
        while j > 0 and A[j - 1] > cur:
            A[j] = A[j - 1]
            j -= 1
            
        A[j] = cur
    return A
        