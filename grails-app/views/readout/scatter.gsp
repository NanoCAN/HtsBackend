<!doctype HTML>
<meta charset = 'utf-8'>
<html>
<head>
    <r:require module="polychart2"/>
    <r:require module="jquery"/>
    <r:layoutResources />
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">

    <style>
    .rChart {
        display: block;
        margin-left: auto;
        margin-right: auto;
        width: 600px;
        height: 400px;
    }
    </style>

</head>
<body>
<div id='dynamicScatter' class='rChart polycharts'></div>
<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>


<r:script>
    var dataUrl = "${g.createLink(controller:"readoutScatter", action: "exportWellReadoutForScatterPlotAsJSON", id:readoutId)}";
    var polyjsData = polyjs.data.url(dataUrl);

    var chartParams = {
        "dom": "dynamicScatter",
        "width": "600",
        "height": "400",
        "layers": [
                {
                    "x": {"var":"Sample"},
                    "y": {"var":"Value"},
                    "data": polyjsData,
                    //"color": "Control",
                    "type": "point",
                    "height":    200
                }
                ],
                "guides": {
                    "x": {
                        "title": "Column",
                        "bw": 1
                    },
                    "y": {
                        "title": "Row",
                        "bw": 1
                    },
                    "color": {
                        "scale": {
                            "type": "gradient2",
                            "lower": "blue",
                            "middle": "yellow",
                            "upper": "red"
                        }
                    }
                },
                "coord": [],
                "id": "dynamicScatter"
            }

    polyjs.chart(chartParams);
</r:script>

<r:layoutResources />
</body>
</html>