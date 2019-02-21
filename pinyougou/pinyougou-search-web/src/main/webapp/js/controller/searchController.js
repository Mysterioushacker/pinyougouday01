app.controller("searchController",function ($scope,$location, searchService) {

    //根据关键字搜索商品
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;

            //构建页面分页导航条信息
            buildPageInfo();
        });
    };

    //搜索对象
    $scope.searchMap = {"keywords":"","category":"","brand":"","spec":{},"price":"","pageNo":1,"pageSize":40,"sortField":"","sort":""};

    //添加过滤条件
    $scope.addSearchItem = function (key, value) {
        if("brand" == key || "category" == key || "price" == key){
            //如果点击的是品牌或者分类的话
            $scope.searchMap[key] = value;
        }else {
            //规格
            $scope.searchMap.pageNo = 1;
            $scope.searchMap.spec[key] = value;
        }

        //点击过滤条件后需要重新搜索
        $scope.search();
    };

    //删除过滤条件
    $scope.removeSearchItem =function (key) {
        if("brand" == key ||"category" == key || "price" == key){
            //如果点击的是品牌或者分类的话
            $scope.searchMap[key] = "";
        }else {
            //规格
            delete $scope.searchMap.spec[key];
        }
        $scope.searchMap.pageNo=1;
        //点击过滤条件后需要重新搜索
        $scope.search();
    };

    //构建页面分页导航条信息
    buildPageInfo = function () {
        //定义要在页面显示的页号的集合
        $scope.pageNoList = [];

        //定义要在页面显示的页号的数量
        var showPageNoTotal = 5;

        //起始页号
        var startPageNo =1;

        //结束页号
        var endPageNo = $scope.resultMap.totalPages;

        //如果总页数大于要显示的页数才有需要处理显示页号数，否则直接显示所有页号
        if($scope.resultMap.totalPages > showPageNoTotal){
            //计算当前页左右间隔页数
            var interval = Math.floor(showPageNoTotal/2);

            //根据间隔得出起始，结束页号
            startPageNo = parseInt($scope.searchMap.pageNo) -interval;
            endPageNo = parseInt($scope.searchMap.pageNo) + interval;

            //处理页号越界
            if(startPageNo > 0){
                if(endPageNo > $scope.resultMap.totalPages){
                    startPageNo = $scope.resultMap.totalPages - (showPageNoTotal-1);
                    endPageNo = $scope.resultMap.totalPages;
                }
            }else {
                endPageNo = showPageNoTotal;
                startPageNo = 1;
            }

            //分页导航条上得前、后的那三个点
            $scope.frontDot = false;
            $scope.backDot = false;

            if(1 < startPageNo){
                $scope.frontDot =true;
            }

            if(endPageNo < $scope.resultMap.totalPages){
                $scope.backDot = true;
            }

            //设置要显示的页号
            for(var i = startPageNo;i<=endPageNo;i++){
                $scope.pageNoList.push(i);
            };
            
            //判断是否为当前页
            $scope.isCurrentPage = function (pageNo) {
                var tmp = parseInt($scope.searchMap.pageNo);
                return tmp == pageNo;
            };
            
            //根据页号查询
            $scope.queryByPag = function (pageNo) {
                if(0<pageNo && pageNo <= $scope.resultMap.totalPages){
                    $scope.searchMap.pageNo = pageNo;
                    $scope.search();
                }
            };
        }
    };
    
    // 排序搜索
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField =sortField;
        $scope.searchMap.sort = sort;
        $scope.search();
    };

    //加载搜索关键字
    $scope.loadKeywords = function () {
        $scope.searchMap.keywords = $location.search()["keywords"];
        $scope.search();
    };
});
