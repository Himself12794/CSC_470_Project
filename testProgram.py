###########################
# Title: Test Program
# Version: 1.0
# Date: today
# Author: Nate Spindler
###########################
'''A test program'''
def main():
    '''The Main function / method.  It runs everything.'''
        
    ## define functions/methods
    def greet_user():
        ''' greets user '''
        global user
        user = input('Hello.  Please enter your name. ')
        return
    def get_choice():
        ''' gets choice '''
        global choice
        choice = input('What would you like to do?' 
                       '\n  h: say hello, g: say goodbye\n  ')
        return            
    def say_hello(i):
        ''' prints output '''
        print('Hello,',i)
        return
    def say_goodbye(i):
        ''' says goodbye, user. '''
        print('Goodbye,',user)
    
    ## run program
    greet_user()
    while True:
        print('*'*80)
        get_choice()
        if choice == 'h':
            say_hello(user)
        if choice == 'g':
            say_goodbye(user)
            exit()

        ## loop back to While True

## tester    
if __name__ == "__main__":
    main() # run main f(x)
    
