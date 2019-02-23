app.controller("payController",function ($scope,cartService) {
    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };
});