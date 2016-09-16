from util.iteration import *

import math

def mean_by_freq(A):

	sum = 0
	total = 0
	for key in A:
		sum += key * A[key]
		total += A[key]

	return sum / total

#A = [59, 69, 43, 51, 63, 55, 62, 62, 53, 67, 54, 48, 47, 49, 60, 66, 69, 66, 53, 68, 64, 67, 43, 61, 55, 53, 47, 43, 49, 43, 48, 62, 44, 55, 69, 67, 63, 53, 51, 62, 43, 54, 59]

def group(A, i, s, e):

	vals = [0,0,0,0,0,0]
	print(vals)
	for val in A:
		x = math.ceil((val+1-s) / i)
		vals[x - 1] += 1

	return vals


def populate_freq(A):

	for key in A:
		vals += [key] * A[key]

	return vals

def cor_coe(A):

	xs = [x[0] for x in A]
	ys = [y[1] for y in A]

	n = len(A)
	x = sum(xs)
	y = sum(ys)
	xy = sum([a*b for (a,b) in A])
	x2 = sum([a*a for a in xs])
	y2 = sum([a*a for a in ys])
	
	m = ((n*xy) - (x * y)) / ((n*x2) - (x**2))

	print("x:",x,"y:",y,"xy:",xy,"x2:",x2,"y2:",y2,"n:",len(A), "m:", m)
	p1 = len(A)*xy-x*y
	p2 = ((len(A)*x2-x**2)**0.5)
	p3 = ((len(A)*y2-y**2)**0.5)

	ans = p1 / (p2 * p3)
	
	print("line of best fit: y="+str(m)+"x"+("-" if ans < 0 else "+")+str(ans)) 
	
	return ans

def bordo(A):
	tally = {}

	for i in range(len(A)):
		curr = A[i]
		for j in range(len(curr)):
			tally.setdefault(curr[j],0)
			tally[j] += len(curr) - curr[j]

	print(tally)

	m = reduce(lambda key1, key2: key1 if tally[key1] > tally[key2] else key2, tally.keys())

	print("Winner is " + str(m) + " with a vote of " + tally[m])


def tally_up(dict, max_only=False):
	tally = {}

	for key in dict.keys():
		for j in range(len(key)):
			tally.setdefault(key[j],0)
			if not max_only: tally[key[j]] += ((len(key) - j) * dict[key])
			elif j == 0:
				print(dict, tally)
				tally[key[j]] += dict[key]

	return tally

def bordo2(dict):

	tally = tally_up(dict)

	m = None
	for item in tally.keys():
		if m == None: m = item
		else: m = m if tally[m] > tally[item] else item
	print("Winner is " + str(m) + " with a vote of " + str(tally[m]))

def remove_smallest_in_key(dict, c):
	newer = {}

	for key, v in dict.items():
		newer[key.replace(c,'')] = v

	return newer

def plurality(dict, recurse=True):
	tally = tally_up(dict, True)

	total = sum(tally.values())

	print(tally)

	print("threshold", total / len(tally))

	fixed = tally.copy()
	for key in fixed.keys():
		if fixed[key] > 0: fixed[key] /= total

	smallest = ('',1.0)
	for key, v in fixed.items():
		if v < smallest[1] and v > 0: smallest = (key,v)

	largest = ('',-1.0)
	doubled = ('',-1.0)
	for key, v in fixed.items():
		if v > largest[1]: largest = (key,v)
		elif v == largest[1] and v > doubled[1]: doubled = (key,v)

	if (doubled[1] == largest[1] or largest[1] <= 0.5) and recurse:
		return plurality(remove_smallest_in_key(dict, smallest[0]))
	else:
		print(fixed)
		print("Largest is " + str(largest[0]) + " with score of " + str(largest[1]))

'''def head_to_head(dict):
	types = []

	for key in dict.values():
		for char in key:
			if not char in types:
				types.append(char)

	for i in range(len(types)):
		for j in range(len(types)):
'''
if __name__ == "__main__":
    A = [59, 69, 43, 51, 63, 55, 62, 62, 53, 67, 54, 48, 47, 49, 60, 66, 69, 66, 53, 68, 64, 67, 43, 61, 55, 53, 47, 43, 49, 43, 48, 62, 44, 55, 69, 67, 63, 53, 51, 62, 43, 54, 59]
    print(A)

