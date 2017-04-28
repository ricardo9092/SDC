SHELL := /bin/bash


all: relatorio.pdf clean

clean: 
	rm -rf *.toc 
	rm -rf *.aux   
	rm -rf *.log  
	rm -rf *.fdb_latexmk 
	rm -rf *.fls
	rm -rf lncs-paper-blx.*
	rm -rf *.bbl *.blg


relatorio.pdf:  relprojLayout.tex 
	latexmk -pdf relprojLayout.tex 
	mv relprojLayout.pdf relatorio.pdf 
