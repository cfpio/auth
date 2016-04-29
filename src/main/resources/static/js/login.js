angular.module('login', ['ngCookies', 'angular-ladda'])
.controller('LoginCtrl', function($scope, $http, $cookies) {
	$scope.loading = false;

	$scope.doLogin = function() {
		$scope.loading = true;

		$http.post('api/tokens', $scope.creds).success(function (data) {
			$scope.status = "connected";

			var originUrl = $cookies.get("originUrl");

			if (originUrl) {
				$cookies.remove("originUrl");
				location.replace(originUrl + location.hash);
			} else {
				location.reload(true);
			}

		}).error(function(data, status) {
			if (status == 404) {
				$scope.status = "notfound";
			} else {
				$scope.status = "error";
				$scope.errorMsg = data.msg ? data.msg : data;
			}
			$scope.loading = false;
		});
	};

});