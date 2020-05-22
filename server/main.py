from flask import Flask, url_for, render_template, request
import json

app = Flask("MapDrawer", static_url_path='')


def add_point(x, y):
	x = float(x)
	y = float(y)
	points = json.load(open('static/coords.json', 'r'))
	points.append({'x': x , 'y': y})
	json.dump(points, open('static/coords.json', 'w'))

@app.route("/")
def index():
	url_for('static', filename='styles.css')
	return render_template("index.html")

@app.route("/add", methods=["POST"])
def add():
	print(request)
	X = None
	Y = None
	X = request.form.get('x')
	Y = request.form.get('y')
	add_point(X, Y)
	return index()



if __name__ == "__main__":
	app.run(host='0.0.0.0', extra_files=['static/coords.json'])