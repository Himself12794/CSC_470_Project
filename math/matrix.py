######################################
#
#	Matrix Class
#	Written by: Philip Whiting
#
######################################

from random import randint

class InvalidMatrix( Exception ):
	'''
	For handling errors with invalid matrix sizes.

	Attributes:
		msg -- Error message
	'''
	def __init__( self, message ):
		Exception.__init__( self, message )
		self.message = message

class Matrix:
	'''
	This class is used to make a representation of a matrix, on which different operations can be performed,
	such as matrix addition, subtraction, and multiplication, as well as solutions to the reduced row matrix,
	and row matrix forms.
	'''
	def __init__( self, initMatrix = [] ):
		'''
		Check if matrix is valid size. If valid, sets matrix. If empty, matrix is blank.
		If initMatrix is a positive integer n, sets the nxn identity matrix.
		'''
		valid = True
		if not type( initMatrix ) is int:
			if len( initMatrix ) != 0:
				for i in range( len( initMatrix ) ):
					curr = len( initMatrix[ i ] )
					prev = len( initMatrix[ i - 1 ] )
					if i != 0:
						if ( curr != prev ) or ( prev  == 0 ):
							valid = False
							break

			if valid:
				self._matrix = initMatrix
				self._m = len( initMatrix )
				if self._m == 0:
					self._n = 0
				else:
					self._n = len( initMatrix[ 0 ] )
			else:
				raise InvalidMatrix( 'Error: Length of rows inconsistent.' )
		elif initMatrix > 1:
			self._identityMatrix( initMatrix )
		else:
			raise InvalidMatrix( 'Error: Invalid matrix format' )

	# Matrix generation
	def randomMatrix( self, m = 3, n = 3, v = 10 ):
		'''Generates an m*n matrix with values between -v and v'''
		temp = []
		for _ in range( m ):
			tempRow = []
			for _ in range( n ):
				tempRow.append( randint( -v, v ) )
			temp.append( tempRow )

		self._matrix = temp
		self._m = m
		self._n = n

	@staticmethod
	def identityMatrix( n ):

		iMatrix = Matrix( n )
		return iMatrix

	def _identityMatrix( self, n ):
		'''Generates the n identity matrix'''
		iMatrix = []
		self._m = n
		self._n = n
		for i in range( n ):
			row = []
			for u in range( n ):
				if u == i:
					row.append( 1 )
				else:
					row.append( 0 )
			iMatrix.append( row )
		self._matrix = iMatrix

	# Methods for solving RRE form and RE form
	def _rowAdd( self, row1, row2 ):
		'''Internal use: adds one row to another'''
		for i, amount in enumerate( row2 ):
			row1[ i ] += amount
		return row1

	def _rowDivide( self, row, divisor ):
		'''Internal use: divides a row by a value'''
		row = [ ( float( value ) / divisor ) for value in row ]
		#print( row )
		return row

	def _rowMultiply( self, row, factor ):
		'''Internal use: multiplies a row by a factor'''
		row = [ ( value * factor ) for value in row ]
		return row

	def _findPivot( self, row ):
		'''Internal use: finds the column id of the pivot position in a row'''
		for index, value in enumerate( row ):
			if value != 0:
				return index
		return None


	def _checkBlank( self, row ):
		'''Internal use: checks if a row is blank'''
		for value in row:
			if value != 0:
				return False
		return True

	def _invert( self ):
		'''Inverts matrix'''
		if self._m == self._n:
			I = Matrix( self._m )

			self.augment( I )
			self.rref()

			for m in range( self._m ):
				self._matrix[ m ] = self._matrix[ m ][ self._m : ]

			self._n = self._m


	# Methods for outside matrix use
	def tran( self ):
		other = Matrix()
		for row in self._matrix:
			other.addColumn( row )
		return other

	def tolist( self ):
		'''Returns matrix as list of lists'''
		return self._matrix

	def issquare( self ):
		'''Checks if matrix is square'''
		return self._m == self._n

	def getSize( self ):
		'''Returns size of matrix'''
		if self.issquare():
			return self._m
		else:
			return self._m, self._n

	def det( self ):
		'''Returns determinant if it exists, else returns none'''
		if not self.issquare():
			return None

		copy = self._matrix[ : ]
		copy.sort()
		copy.reverse()

		for index in range( len( copy ) ):
			pivot_column = self._findPivot( copy[ index ] )
			if pivot_column == None:
				continue
			pivot = copy[ index ][ pivot_column ]

			for i in range( index + 1, len( copy ) ):
				if not self._checkBlank( copy[ i ] ):
					below = copy[ i ][ pivot_column ]
					addend = self._rowMultiply( copy[ index ], -below )
					addend = self._rowDivide( addend, pivot )
					copy[ i ] = self._rowAdd( addend, copy[ i ] )
				else:
					return 0

		product = 1
		for I in range( len( copy ) ):
			product *= copy[ I ][ I ]
		product *= (-1) ** ( I + 1 )
		return product

	def ref( self ):
		'''Converts matrix to row echelon form'''
		if self._n >= self._m:

			copy = self._matrix[ : ]
			copy.sort()
			copy.reverse()

			for index in range( len( copy ) ):
				pivot_column = self._findPivot( copy[ index ] )
				if pivot_column == None:
					continue
				pivot = copy[ index ][ pivot_column ]
				copy[ index ] = self._rowDivide( copy[ index ], pivot )
				for i in range( index + 1, len( copy ) ):
					if not self._checkBlank( copy[ i ] ):
						addend = self._rowMultiply( copy[ index ], -copy[ i ][ pivot_column ] )
						copy[ i ] = self._rowAdd( addend , copy[ i ] )

			copy.sort()
			copy.reverse()
			self._matrix = copy
		else:
			raise InvalidMatrix( 'Error: cannot convert %dX%d matrix to RREF' % ( self._m, self._n ) )

	def rref( self ):
		'''Converts matrix to reduced row echelon form'''
		if not self._n >= self._m:
			raise InvalidMatrix( 'Error: cannot convert %dX%d matrix to RREF' % ( self._m, self._n ) )
		else:
			self.ref()
			copy = self._matrix[ : ]
			for index in reversed( range( len( copy ) ) ):
				pivot_column = self._findPivot( copy[ index ] )
				if not pivot_column:
					continue

				for i in reversed( range( index ) ):
					if not self._checkBlank( copy[ i ] ):
						copy[ i ] = self._rowAdd( self._rowMultiply( copy[ index ], -copy[ i ][ pivot_column ] ), copy[ i ] )

			self._matrix = copy

	def solve( self, b = None ):
		'''Solves and returns x in the formula Ax=b.'''
		copy = Matrix( self._matrix )
		if not b:
			if copy._m + 1 == copy._n:
				copy.rref()
				answ = []
				for row in copy._matrix:
					answ.append( row[ -1 ] )
				return answ
		elif type( b ) is list:
			copy.addColumn( b )
			copy.rref()
			answ = []
			for row in copy._matrix:
				answ.append( row[ -1 ] )
			return answ
		else:
			raise TypeError( 'Unsupported %s for b in Matrix.solve( b )' % type( b ) )

	def addRow( self, row ):
		'''Adds a row to end of matrix'''
		if len( row ) == self._n or self._n == 0:
			self._matrix.append( row )
			if self._n == 0:
				self._n = len( row )
			self._m += 1
		else:
			raise InvalidMatrix( 'Error: Row has %d values, needs %d values.' % ( len( row ), self._n ) )

	def addColumn( self, column ):
		'''Adds column to end of matrix'''
		if len( column ) == self._m or self._n == 0:
			if self._n == 0:
				for i in column:
					self._matrix.append( [] )
					self._m = len( column )

			for i in range( self._m ):
				self._matrix[ i ].append( column[ i ] )

			self._n += 1
		else:
			raise InvalidMatrix( 'Error: Column has %d values, needs %d values.' % ( len( column ), self._m ) )

	def augment( self, other ):
		'''Augments the matrix with another'''
		if isinstance( other, Matrix ):
			new = self._matrix
			if other._m == self._m:
				for i in range( self._m ):
					new[ i ] = self._matrix[ i ] + other._matrix[ i ]
			self._matrix = new
			self._n += other._n
		else:
			raise InvalidMatrix( 'Error: Cannot augment %dX%d matrix with a %dX%d matrix' % ( self._m, self._n, other._m, other._n ) )

	def homog( self ):
		'''Solves the homogenous equation Ax=0'''
		other = Matrix( self._matrix )
		col = []
		for _ in range( self._m ):
			col.append( 0 )

		other.addColumn(col)
		other.rref()
		self._matrix = other._matrix
		self._n += 1

	def __add__( self, other ):
		if isinstance( other, Matrix ):
			dupe = self._matrix
			if ( self._n == other._n ) and ( self._m == other._m ):
				for i in range( self._m ):
					for index, amount in enumerate( other._matrix[ i ] ):
						dupe[ i ][ index ] += amount

				return Matrix( dupe )
			else:
				raise InvalidMatrix( 'Matrix of size %dX%d incompatible for addition with matrix of size %dX%d' % ( self._m, self._n, other._m, other._n ) )
		else:
			raise TypeError( 'Cannot add matrix object with non-matrix type \'%s\'.'  % type( other ).__name__ )

	def __sub__( self, other ):
		if isinstance( other, Matrix ):
			dupe = self._matrix
			if ( self._n == other._n ) and ( self._m == other._m ):
				for i in range( self._m ):
					for index, amount in enumerate( other._matrix[ i ] ):
						dupe[ i ][ index ] -= amount

				return Matrix( dupe )
			else:
				raise InvalidMatrix( 'Error: Matrix of size %dX%d incompatible for addition with matrix of size %dX%d' % ( self._m, self._n, other._m, other._n ) )
		else:
			raise TypeError( 'Cannot get difference of matrix object and non-matrix type \'%s\'.' % type( other ).__name__ )

	def __mul__( self, other ):
		if isinstance( other, Matrix ):
			if self._n == other._m:
				new = []
				for i in range( self._m ):
					row = []
					for i2 in range( other._n ):
						value = 0
						for i3 in range( self._n ):
							factor1 = self._matrix[ i ][ i3 ]
							factor2 = other._matrix[ i3 ][ i2 ]
							addend = factor1 * factor2
							value += addend
						row.append( value )
					new.append( row )

				return Matrix( new )
			else:
				raise InvalidMatrix( 'Matrix of size %dX%d not compatible for multiplication with matrix of size %dX%d.' % ( self._m, self._n, other._m, other._n ) )
		else:
			raise TypeError( 'Cannot multiply matrix object with invalid type \'%s\'' % type( other ).__name__ )

	def __rmul__( self, factor ):
		if type( factor ) is int:
			new = []
			for row in self._matrix:
				new.append( self._rowMultiply( row, factor ) )
			return Matrix( new )

	def __pow__( self, exponent ):
		'''Raise matrix object to -1 to invert, raise to 0 to get identity matrix of same size.'''
		if type( exponent ) is not int:
			raise TypeError( 'Unsupported operand type for Matrix object and type \'%s\'' % type( exponent ).__name__ )

		if not self.issquare():
			return None

		if exponent == 0:
			return Matrix( self._m )

		elif exponent == -1:
			#if self.det() != 0:
			other = Matrix( self._matrix )
			other._invert()
			return other

			#else:
			#	return None

		elif exponent >= 1:
			product = 1
			for _ in range( exponent ):
				product = product * self
			return product

	def __str__( self ):
		output = ''
		for row in self._matrix:
			printed = '[  '
			for value in row:
				if value == 0:
					printed += '0.00  '
				else:
					printed += '%0.2f  ' % value
			output += printed + '  ]\n'
		return output

	def __eq__( self, other ):
		if isinstance( other, Matrix ):
			return self._matrix == other._matrix
		else:
			raise TypeError('Unsupported comparison for Matrix object and type \'%s\'' % type( other ).__name__ )


if __name__ == '__main__':

	#Testing Correct Matrix Initialization
	A = [
			[ 1, -4, 2 ],
			[ -2, 8, -9 ],
			[ -1, 7, 0 ],
		]

	BM = [
			[ 1, 2, 3 ],
			[ 1, 2 ]
		]

	F = [
			[ 2, -8, 6, 8 ],
			[ 3, -9, 5, 10 ],
			[ -3, 0, 1, -2 ],
			[ 1, -4, 0, 6 ],
		]


	A = Matrix( A )
	if A.getSize() != 3:
		print( 'Incorrect dimensions read for initializing with list of lists' )

	B = Matrix()
	B.addColumn( [ 3, 5 ] )
	B.addColumn( [ 7, 2 ] )
	B.addRow( [ 3, 1 ] )
	if B.getSize() != ( 3, 2 ):
		print( 'Incorrect initialization for initializing blank and add rows and columns later' )

	I2 = [ [ 1, 0 ], [ 0, 1 ] ]
	C = Matrix( 2 )
	if C.tolist() != I2:
		print( 'Initializing with only a number does not give correct identity matrix' )

	try:
		BM = Matrix( BM )
		print( 'Matrix initialized with invalid matrix' )
	except InvalidMatrix:
		pass

	# Testing the determinant
	if A.det() != 15.0:
		print( 'Calculated wrong determinant' )

	F = Matrix( F )
	if int( F.det() ) != -35:
		print( 'Calculated wrong determinant' )

	if B.det() != None:
		print( 'Attempted to calculate determinant of invalid matrix' )

	# Testing ref and rref
	A.rref()
	if A.tolist() != Matrix( 3 ).tolist():
		print( 'RREF does not work correctly' )

		# Test for non-square matrix

	# Testing solve()


	# Testing augment()


	# Testing multiplication


	# Testing addition


	# Testing subtraction


	# Testing exponents
