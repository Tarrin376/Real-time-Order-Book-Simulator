# Build with Python
FROM python:3.13-slim

WORKDIR /app

# Copy the requirements.txt file to install dependencies
COPY requirements.txt .

# Install all dependencies (to keep Docker image small, tell pip not to use cache)
RUN pip install --no-cache-dir -r requirements.txt

# Copy the source code
COPY main.py .

# Command to run the trading client
CMD ["python", "main.py"]