# -*- coding: utf-8 -*-

NUM_OF_CARS = 50

print """<?xml version="1.0"?>
<!--
	czas trwania schematu: 12h = 21600
	
	pomiędzy każdą parą węzłów generowane jest %d samochodów
	z rozkładem jednostajnym
-->
<traffic>
""" % (NUM_OF_CARS,)

for fromId in xrange(0,14):
    for toId in xrange(0,14):
        if fromId == toId:
            continue

        schemeStr = """
	<scheme count='%d'>
		<gateway id='%s'>
			<uniform a='0' b='7000' />
		</gateway>
		<gateway id='%s' />
	</scheme>
        """ % ( NUM_OF_CARS, "G"+str(fromId), "G"+str(toId) )

        print schemeStr

print "</traffic>"
