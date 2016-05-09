<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>StaTweetstics</title>

    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/bootstrap-switch.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/vex.css">
    <link rel="stylesheet" href="css/vex-theme-wireframe.css">
    <link rel="stylesheet" href="css/main.css">

    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
    <div id="particles"></div>
    
    <div id="tweetbox">
        <div id="tweetWrite">
            <input type="text" id="tweetInput" placeholder="Your tweet here!" autocomplete="off">

            <a id="tweetButton" onmousedown="changeURL(this)">
                <div>
                    <i class="fa fa-twitter fa-2x"></i>
                </div>
            </a>
        </div>
    </div>
    
    <div class="container-fluid" id="content">
        <div class="row-fluid">
            <div class="col col-sm-10" id="suggestions">
                <div class="centerthis">
                <h1>StaTweetstically speaking, <br>it'd be cool to tweet about...</h1>
                
                <ul id="topsugslist"></ul>
                <p id="topsugsslide"></p>
                
                <input type="checkbox" name="list-or-slide" checked data-on-color="success" data-off-color="success" data-handle-width="31" data-on-text="list" data-off-text="slide" style="margin-left: 20px">
                    
                <input type="checkbox" name="like-or-retweet" checked data-on-color="success" data-off-color="success" data-handle-width="53" data-on-text="retweets" data-off-text="likes" style="margin-left: 20px">
                
                <div id="usernameDisplay">
                    <input type="checkbox" name="indiv-or-compare" checked data-on-color="success" data-off-color="success" data-handle-width="95" data-on-text="individual user" data-off-text="compare users" style="margin-left: 20px">
                    <p>for</p>
                    <h3>@</h3>
                    <div id="inputGroup">
                        <input type="text" class="usernameInput" placeholder="username" autocomplete="off" readonly="true"  onkeypress="this.style.width = ((this.value.length + 1) * 10) + 'px';">
                    </div>
                    <i class="fa fa-plus" id="usernameAdd"></i>
                    <i class="fa fa-pencil" id="usernameEdit"></i>
                </div>

<!--
                <div id="sugsgroup" class="radiogroup">
                    <ul>
                        <li>
                            <input type='radio' name='num-users' value='indivuser' id='indivuser' checked="checked"/>
                            <label for='indivuser'><div>individual user</div></label>
                        </li>
                        <li>
                            <input type='radio' name='num-users' value='compareusers' id='compareusers'/>
                            <label for='compareusers'><div>compare users</div></label>
                        </li>
                    </ul>
                </div>
-->
                    </div>
            </div><!--
            
            --><div class="col col-sm-5 triangle-border left" id="tweetStats">
                <p id="closeStats">x</p>
                <h3 id="statsTitle">Stats:</h3>
                <br>
                <div>
                    <h5>Avg Retweets:</h5>
                    <h1>38</h1>
                </div>
                <br>
                <div>
                    <h5>Top Tweet:</h5>
                    <h6>'I'm all yours tonight!' she cried, 'Do something to me you'd never normally dare do to a woman!' 'All right' he said, and made eye contact. With this it's 160.</h6>
                </div>
            </div>
        </div>
    </div>

    <script src="js/jquery-2.2.2.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/bootstrap-switch.min.js"></script>
    <script src="js/jquery.particleground.min.js"></script>
    <script src="js/vex.combined.min.js"></script>
    <script>vex.defaultOptions.className = 'vex-theme-wireframe';</script>
    <script src="js/main.js"></script>
</body>
</html>