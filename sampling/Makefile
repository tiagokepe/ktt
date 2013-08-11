all: compile package run

compile:
	javac -d classes/sample-random src/test/SamplingRandom.java

package:
	jar -cvf jar/sample-random.jar -C classes/sample-random .

run:
	hadoop jar jar/sample-random.jar test.SamplingRandom $(percentage) /kepe/input /kepe/sample
