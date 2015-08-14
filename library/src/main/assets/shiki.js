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



});