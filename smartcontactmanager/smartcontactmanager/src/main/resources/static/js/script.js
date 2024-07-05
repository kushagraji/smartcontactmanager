console.log("this is script file");
const toggleSidebar = () => {
					    if($(".sidebar").is(":visible")){
					        $(".sidebar").css("display", "none");
					        $(".content").css("margin-left", "0%");
					    }else {
					        $(".sidebar").css("display", "block");
					        $(".content").css("display", "20%");
					    }
					};

const search = () => {

	console.log("searching");
}					


					