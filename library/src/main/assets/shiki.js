$().ready(function(){
    $(".b-spoiler label").click(function(){
        $(this).next("div.content").show();
        $(this).hide();
    });
    $(".b-spoiler label, .b-spoiler div.content div.before").css("font-size", "18px");

    $(".b-spoiler div.content div.before").click(function(){
        $(this).parent().hide();
        $(this).parent().parent().find("label").show();
    });

    $.each($("body").find("a"), function(){
        var href = $(this).attr("href");
        if(href != "" && !href.match(/http:/)){
            $(this).attr("href", "http://shikimori.org"+href);
        }
    });

    $.each($("body").find("img"), function(){
        var href = $(this).attr("src");
        if(href != "" && !href.match(/http:/)){
            $(this).attr("src", "http://shikimori.org"+href);
        }
    });

    function getImageUrl(src){
        if(src.match(/image=1/))
            return src;
        
        var pref;
        if(src.match(/\?/)){
            pref = src+"&image=1";
        } else {
            pref = src+"?image=1";
        }
        return pref;
    }

    function loadImage(src){
        var pref = getImageUrl(src);
        window.open(pref,"_self");
    }

    $("img.b-poster").click(function(){
        var src = $(this).attr("src");
        loadImage(src);
        return true;
    });

    $("a.b-image").click(function(){
        var src = $(this).attr("href");
        $(this).attr("href", getImageUrl(src));
    });

});