import subprocess
from flask import Flask, request
from flask_restful import reqparse, abort, Api, Resource
import json
import sys
import logging
import py_eureka_client.eureka_client as eureka_client


command = "ffmpeg -i audio32.webm -ab 160k -ac 1 -ar 44100 -vn audio1.wav"

subprocess.call(command, shell=True)