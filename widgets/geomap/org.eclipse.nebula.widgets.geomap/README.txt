SWT MapWidget
=============



Java based browsing of openstreetmap tiles (or the like). This is a EPL licensed SWT-Widget meant for reuse. A single file is enough to bring mapping to your Eclipse, swt, jface, RCP application. Sourcecode is provided as is and without warranty.
Only requirement is SWT so you don't need the full eclipse rcp stack. 

- This project contains 2 things:

1. MapWidget is the standalone SWT Control that can show a map from
   any tile-server.
2. MapBrowser is the example showcase for the MapWidget. 

Note that data is loaded from external servers 
  http://gazetteer.openstreetmap.org/,
  http://tile.openstreetmap.org/ and 
  http://tah.openstreetmap.org.
  
The MapWidget is just a very simple Java Viewer for the data provided by the above servers.
Please check and comply to their usage-policies. Their data originates again from openstreetmap.org,
which is a free source of data. However using the above tile-servers is subject to their
usage policies. You can set up your own tileserver by following the instructions on 
openstreetmap.org.

You can trivally supply your own TileServer by creating an instance of the class
MapWidget.TileServer. If you have a local-dataset (eg for offline access) you can return
local file Urls from your tileserver. You can also change the rather simple code to allow
for some other more direct look-up, if going through an URL seems inappropriate for your
particular scenario.

If you see error messages saying that tileservers cannot be reached this can be caused by either
a complete lack of an internet connection (in which case you are out of luck) or by a restricted
connection where you are forced to use a http(s) proxy-server. 
The later is often the case in companies which do not allow unrestricted access to the internet.
In this case you need to set the system properties httpProxy.host and maybe also httpProxy.port 
via -D command line options. For https these properties are named https.proxyHost and https.proxyPort.
You can also set these properties programmatically from java. An excellent in depth explanation can 
be found at http://java.sun.com/javase/6/docs/technotes/guides/net/proxies.html.



- Developer info

The entire viewer runs using standard jdk version 1.6 or newer. Sourcecode can be found below. Only dependency is SWT.

	Sourceforge project page: http://sourceforge.net/projects/mappanel/



- Epilog

Acknowledgments for feedback, testing, fixing, suggestions go to: 
	Everyone at openstreetmap.org, Ricky Clarkson, Werner de Bruijn.



Contact author: stepan.rutz AT gmx.de . Feedback etc is more than welcome.

