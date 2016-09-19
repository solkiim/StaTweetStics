var username;

var indivuser;
var indivuserlikes = {};
var indivuserretweets = {};

var compareusers;
var compareuserslikes = {};
var compareusersretweets = {};
var usersToCompare = [];
var userCount = 1;

var indiv = true;   // individual user vs group
var RTnotLike = true;   // retweets not likes

var displayedSugs;    // sugs displayed currently

$(document).ready(function() {
    indiv = true;
    
    // particle background setup
    $('#particles').particleground({
        dotColor: '#F0F4FF',
        lineColor: '#F0F4FF' /*E1E9FF*/
    });
    
    dialogBoxes();
});

function dialogBoxes() {
    // username dialog prompt
    vex.dialog.buttons.YES.text = "let's go!"
    
    vex.dialog.confirm({
        message: 'Click on the suggestions to see statistics!'
    });
    
    vex.dialog.buttons.YES.text = "i'm ready!"
        
    vex.dialog.prompt({
        message: 'What twitter handle do you wanna search?',
        placeholder: 'username',
        callback: function(value) {
            if (value !== false) {  // if valid username was entered
                username = value;
                $(".usernameInput").val(value); // set username
                getIndivUser();
            } else {    // if no valid username was entered
                indivuser = {"enter":[], "a twitter handle":[], "to see":[], "super cool":[], "stats!":[]}; // set default message
                compareusers = indivuser;
                displayedSugs = indivuser;
                topSugList();
                topSugSlide();
            }
        }
    });
}

function errorAlert() { // if username is invalid
    vex.dialog.buttons.YES.text = "try again!"
    
    vex.dialog.confirm({
        message: 'Oops! That username is invalid.'
    });
}


/*------------------ GETTING TRENDING LISTS ------------------*/
// getting individual user trending lists
function getIndivUser() {
    // sending the username to the backend
    var postParameters = {'user': username};
    $.get("/userTweets", postParameters, function(responseJSON) {
        var parsedResponse = JSON.parse(responseJSON);
        
        if (parsedResponse.indivLikes.length == 0) {   // if username is invalid
            $(".usernameInput").val("");
            $(".usernameInput").css("width", "90px");
            errorAlert();
        } else {
            var parsedIndivRetweets = parsedResponse.indivRetweets;
            var parsedIndivLikes = parsedResponse.indivLikes;

            // clear data for previous username
            indivuser = {};
            indivuserretweets = {};
            indivuserlikes = {};

            // get and save the new comparison data
            for (var i = 0; i < parsedIndivRetweets.length; i++) {
                indivuserretweets[parsedIndivRetweets[i].text] = parsedIndivRetweets[i];
            }
            for (var i = 0; i < parsedIndivLikes.length; i++) {
                indivuserlikes[parsedIndivLikes[i].text] = parsedIndivLikes[i];
            }

            // set current data to correct metric
            if (RTnotLike) {
                indivuser = indivuserretweets;
            } else {
                indivuser = indivuserlikes;
            }

            displayedSugs = indivuser;
            topSugList();
            topSugSlide();
        }
    })
}

// getting compare users trending lists
function getCompareUsers() {
    // sending the usernames to the backend
    var postParameters = {'usernames': JSON.stringify(usersToCompare)};
    $.get("/compareUserTweets", postParameters, function(responseJSON) {
        var parsedResponse = JSON.parse(responseJSON);
        
        if (parsedResponse.indivLikes.length == 0) {   // if username is invalid
            $(".usernameInput").val("");
            $(".usernameInput").css("width", "90px");
            errorAlert();
        } else {
            var parsedCompRetweets = parsedResponse.indivRetweets;
            var parsedCompLikes = parsedResponse.indivLikes;

            // clear data for previous comparison
            compareusers = {};
            compareuserslikes = {};
            compareusersretweets = {};

            // get and save the new comparison data
            for (var i = 0; i < parsedCompRetweets.length; i++) {
                compareusersretweets[parsedCompRetweets[i].text] = parsedCompRetweets[i];
            }
            for (var i = 0; i < parsedCompLikes.length; i++) {
                compareuserslikes[parsedCompLikes[i].text] = parsedCompLikes[i];
            }

            // set current data to correct metric
            if (RTnotLike) {
                compareusers = compareusersretweets;
            } else {
                compareusers = compareuserslikes;
            }

            displayedSugs = compareusers;
            topSugList();
            topSugSlide();
        }
    })
}


/*------------------ TWEET COMPOSITION ------------------*/
changeURL = function(e) {
    var tweetContent = document.getElementById("tweetInput").value;
    var tcURI = encodeURI(tweetContent);
    var tweetURL = "https://twitter.com/intent/tweet?text=" + tcURI;
    e.href = tweetURL;
};


/*------------------ STATS TOGGLE ------------------*/
var statsOut = false;

// when slide-list toggle switch clicked
$(document).on("click", "#topsugslist li, #topsugsslide", function() {
    $("#statsTitle").html($(this).text() + " Stats:");
    makeTrendGraph($(this).text());
    
    // display data for correct metric
    if (RTnotLike) {
        $("#avgtitle").html("avg retweets:");
        var avgrt = displayedSugs[$(this).text()].avgRT;
        avgrt = Math.round(avgrt * 100) / 100;
        $("#avgvalue").html(avgrt);
        
        $("#toptweetvalue").html(displayedSugs[$(this).text()].tweetTextRT);
        if (!indiv) {   // add author if comparing
            $("#toptweetvalue").append("<br>-@");
            $("#toptweetvalue").append(displayedSugs[$(this).text()].nameRT);
        }
    } else {
        $("#avgtitle").html("avg likes:");
        var avglk = displayedSugs[$(this).text()].avgLK;
        avglk = Math.round(avglk * 100) / 100;
        $("#avgvalue").html(avglk);
        
        $("#toptweetvalue").html(displayedSugs[$(this).text()].tweetTextLK);
        if (!indiv) {   // add author if comparing
            $("#toptweetvalue").append("<br>-@");
            $("#toptweetvalue").append(displayedSugs[$(this).text()].nameLK);
        }
    }
    
    if (!statsOut) {
        $("#tweetStats").slideToggle(400);
        $("#suggestions").attr("class", "col col-sm-7");
        $("#tweetStats").css("display", "block");
        statsOut = true;
    }
});

// when close status button clicked
$("#closeStats").click(function() {
    $("#tweetStats").toggle();
    $("#suggestions").attr("class", "col col-sm-12");
    $("#tweetStats").css("display", "none");
    statsOut = false;
});


/*------------------ SUGGESTIONS LIST SLIDE ------------------*/
// populates the top sug list
function topSugList() {
    var listhtml = "";
    var wordlist = Object.keys(displayedSugs);
    for (var i = 0; i < wordlist.length; i++) {
        listhtml += ("<li>" + wordlist[i] + "</li>");
    }
    $("#topsugslist").html(listhtml);
}

var timer;  // keeps track of current cycle's interval

// generates and starts the top sug slide
function topSugSlide() {
    if (timer !== undefined) {
        clearInterval(timer);   // stop previous cycle
    }
    timer = cycle();    // set new cycle
}

function cycle() {
    var i = 0;
    var wordlist = Object.keys(displayedSugs);
    function run() {
        $("#topsugsslide").html(wordlist[i]);
        i++;
        if (i === wordlist.length) {
            i = 0;
        }
    }
    return setInterval(run, 2500);
}


/*------------------ USERNAME INPUT ------------------*/
// resize input field based on the input typed
function resizeInput() {
    $("input").autoresize({padding:20,minWidth:40,maxWidth:300});
}

$.fn.textWidth = function(_text, _font){//get width of text with font.  usage: $("div").textWidth();
    var fakeEl = $('<span>').hide().appendTo(document.body).text(_text || this.val() || this.text()).css('font', _font || this.css('font')),
        width = fakeEl.width();
    fakeEl.remove();
    return width;
};

$.fn.autoresize = function(options){
    options = $.extend({padding:10,minWidth:0,maxWidth:10000}, options||{});
    $(this).on('input', function() {
        $(this).css('width', Math.min(options.maxWidth,Math.max(options.minWidth,$(this).textWidth() + options.padding)));
    }).trigger('input');
    return this;
}

var editingUsername = false;

// editing username via page form
$("#usernameEdit").click(function() {
    if (editingUsername) {  // done editing username
        editingUsername = false;
        $("#usernameEdit").attr("class", "fa fa-pencil");
        $(".usernameInput").prop("readonly", true);
        $(".usernameInput").css("border-bottom","none");
        $("#usernameAdd").css("display", "none");
        $("#closeStats").click();
        username = $(".usernameInput").val();
        
        if (indiv) {
            $("#div-chart").css("display","none");
            $("#tweetStats h5").css("margin-top","40px");
            $("#tweetStats h1").css("font-size","60px");
            $("#tweetStats").css("max-height","470px");
            $("#tweetStats").css("top","calc(50% - 360px)");
                    
            getIndivUser();
        } else {
            $("#div-chart").css("display","block");
            $("#tweetStats h5").css("margin-top","30px");
            $("#tweetStats h1").css("font-size","45px");
            $("#tweetStats").css("max-height","680px");
            $("#tweetStats").css("top","calc(50% - 500px)");
            
            usersToCompare = [];
            $('#inputGroup > input').each(function () {
                if (this.value != "") {
                    usersToCompare.push(this.value);
                }
            });
            getCompareUsers();
        }
    } else {
        editingUsername = true;
        $("#usernameEdit").attr("class", "fa fa-check");
        $(".usernameInput").prop("readonly", false);
        $(".usernameInput").css("border-bottom","1px solid #162252");
        
        userCount = 1;
        if (!indiv) {
            $("#usernameAdd").css("display", "inline-block");
        }
    }
});


/*------------------ USERNAME ADD ------------------*/
// when add username input button clicked
$("#usernameAdd").click(function() {
    if (userCount < 4 && !indiv) {  // max 5 users
        userCount = userCount + 1;
        $("#inputGroup").append('<h3>, </h3>');
        $("#inputGroup").append('<input type="text" class="usernameInput" placeholder="username" autocomplete="off" style="border-bottom: 1px solid #162252" onkeypress="$(this).autoresize({padding:0,minWidth:0,maxWidth:300});">'); //add input box
    }
    if (userCount == 4) {
        $("#usernameAdd").css("display", "none");
    } else {
        $("#usernameAdd").css("display", "inline-block");
    }
});


/*------------------ SLIDE OR LIST ------------------*/
$("[name='list-or-slide']").bootstrapSwitch();
$(".bootstrap-switch-label").html("<div></div>");
$(".bootstrap-switch").css("background","#162252");

// when slide-or-list toggle switch clicked
$("input[name='list-or-slide']").on("switchChange.bootstrapSwitch", function(event, state) {
    $("#closeStats").click();
    if (state) {    // if switched to list
        topSugList();
    } else {        // if switched to slide
        topSugSlide();
    }
    $("#topsugslist").slideToggle(500);
    $("#topsugsslide").slideToggle(500);
});


/*------------------ LIKE OR RETWEET ------------------*/
$("[name='like-or-retweet']").bootstrapSwitch();
$(".bootstrap-switch-label").html("<div></div>");
$(".bootstrap-switch").css("background","#162252");

// when like-or-retweet toggle switch clicked
$("input[name='like-or-retweet']").on("switchChange.bootstrapSwitch", function(event, state) {
    $("#closeStats").click();
    if (state) {    // if switched to retweets
        RTnotLike = true;
        indivuser = indivuserretweets;
        compareusers = compareusersretweets;
    } else {        // if switched to likes
        RTnotLike = false;
        indivuser = indivuserlikes;
        compareusers = compareuserslikes;
    }
    
    if (indiv) {
        displayedSugs = indivuser;
    } else {
        displayedSugs = compareusers;
    }
    topSugList();
    topSugSlide();
});


/*------------------ INDIV OR COMPARE ------------------*/
$("[name='indiv-or-compare']").bootstrapSwitch();
$(".bootstrap-switch-label").html("<div></div>");
$(".bootstrap-switch").css("background","#162252");

// when indiv-or-compare toggle switch clicked
$("input[name='indiv-or-compare']").on("switchChange.bootstrapSwitch", function(event, state) {
    if (state) {    // if switched to individual
        indiv = true;
        $("#inputGroup").html('<input type="text" class="usernameInput" placeholder="username" autocomplete="off" readonly="true"  onkeypress="$(this).autoresize({padding:0,minWidth:0,maxWidth:300});">');
        displayedSugs = indivuser;
        $(".fa").click();
    } else {        // if switched to compare
        indiv = false;
        displayedSugs = compareusers;
        $(".fa").click();
    }
});
