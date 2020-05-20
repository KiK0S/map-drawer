from flask import Flask, url_for, render_template, request


app = Flask("MapDrawer")

points = []

@app.route("/")
def index():
	url_for('static', filename='styles.css')
	return render_template("index.html", points=points)

@app.route("/add", methods=["GET", "POST"])
def add():
	print(request)
	X = None
	Y = None
	# if request.method == "GET":
	X = request.form.get('x')
	Y = request.form.get('y')
	# else:
	# 	X = request.['x']
	# 	Y = request.form['y']
	points.append({"x": X, "y": Y})
	return index()



if __name__ == "__main__":
    app.run(host='0.0.0.0')