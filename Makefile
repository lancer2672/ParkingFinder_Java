.PHONY: build_docker
build_docker:
	docker build -t parking_finder .
	docker tag parking_finder lancer2672/parking_finder:latest
	docker push lancer2672/parking_finder:latest