import subprocess
import os

# Define the Java class name and file name
JAVA_CLASS_NAME = 'Main'  # Replace with your actual class name
JAVA_FILE_NAME = JAVA_CLASS_NAME + '.java'
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))

# Path to the directory containing your Java source code
JAVA_SRC_DIR = os.path.abspath(os.path.join(SCRIPT_DIR, '../'))

# Directory to output compiled class files
JAVA_BIN_DIR = os.path.abspath(os.path.join(SCRIPT_DIR, '../../bin'))

# Ensure the bin directory exists
os.makedirs(JAVA_BIN_DIR, exist_ok=True)
# Define the test cases as a list of dictionaries
test_cases = [
                ['8', 'd', '15'],

#             8, 16, 32, 41, and 52
#                 ['8', 'a', '15'],   # Out of memory
#                 ['16', 'a', '15'],  # Out of memory
#                 ['32', 'a', '15'],  # Out of memory
#                 ['41', 'a', '15'],  # Out of memory
#                 ['52', 'a', '15'],  # Out of memory
#
#                 ['8', 'b', '15'],   # Timeout
#                 ['16', 'b', '15'],  # Timeout
#                 ['32', 'b', '15'],  # Timeout
#                 ['41', 'b', '15'],  # Timeout
#                 ['52', 'b', '15'],  # Timeout
#
#                 ['8', 'c', '15'],   # Success
#                 ['16', 'c', '15'],  # Success
#                 ['32', 'c', '15'],  # Success
#                 ['41', 'c', '15'],  # Fail
#                 ['52', 'c', '15'],  # Fail
#
#                 ['8', 'd', '15'],   # Success
#                 ['16', 'd', '15'],  # Success
#                 ['32', 'd', '15'],  # Success
#                 ['41', 'd', '15'],  # Success
#                 ['52', 'd', '15'],  # Success

            ]

def compile_java():
    """Compiles the Java code into the bin directory."""
    print('Compiling Java code...')
    javac_cmd = [
        'javac',
        '-cp', '..',
        '-d', JAVA_BIN_DIR,                # Specify output directory for class files
        os.path.join(JAVA_SRC_DIR, JAVA_FILE_NAME)  # Path to the Java source file
    ]
    compile_process = subprocess.run(javac_cmd,
                                     capture_output=True,
                                     text=True)
    if compile_process.returncode != 0:
        print('Compilation failed:')
        print(compile_process.stderr)
        return False
    print('Compilation succeeded.')
    return True

def run_java(args):
    """Runs the Java program with the specified arguments."""
    cmd = [
        'java',
        '-cp', JAVA_BIN_DIR,   # Set classpath to the bin directory
        JAVA_CLASS_NAME
    ] + args
    run_process = subprocess.run(cmd,
                                 capture_output=True,
                                 text=True)
    return run_process

def main():
    # Print Python's current working directory
    print("Python script's current working directory:", os.getcwd())
    print("Java source directory:", JAVA_SRC_DIR)
    print("Java bin (class files) directory:", JAVA_BIN_DIR)

    # Check if Java source file exists
    java_file_path = os.path.join(JAVA_SRC_DIR, JAVA_FILE_NAME)
    if not os.path.isfile(java_file_path):
        print(f'Error: {JAVA_FILE_NAME} not found in {JAVA_SRC_DIR}.')
        return

    # Compile the Java code
    if not compile_java():
        return

    # Run test cases
    for args in test_cases:
        print(f'\nRunning test case with Arguments: {args}')

        # Run the Java program
        result = run_java(args)

        # Capture and display both stdout and stderr
        output = result.stdout.strip()
        error_output = result.stderr.strip()

        if output:
            print('Standard Output:')
            print(output)

        if error_output:
            print('Standard Error:')
            print(error_output)

if __name__ == '__main__':
    main()
