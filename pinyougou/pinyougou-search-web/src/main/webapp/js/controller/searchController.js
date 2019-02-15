app.controller("searchController",function ($scope, searchService) {

    //根据关键字搜索商品
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
        });
    }
});