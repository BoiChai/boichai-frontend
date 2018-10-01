package api

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"os"
	"fmt"
)

/**
 * := Coded with love by Sakib Sami on 25/5/18.
 * := root@sakib.ninja
 * := www.sakib.ninja
 * := Coffee : Dream : Code
 */

var routes *gin.Engine

func NewRoutes() {
	gin.SetMode(os.Getenv("BUILD_KIND"))

	routes = gin.Default()
	routes.Use(gin.Logger())
	routes.Use(gin.Recovery())

	if gin.IsDebugging() {
		routes.LoadHTMLGlob("templates/*")
		routes.Static("/static", "./public")
	} else {
		routes.LoadHTMLGlob("/etc/templates/*")
		routes.Static("/static", "/etc/public")
	}

	routes.GET("/", index)
	routes.GET("/contact", contact)
	routes.GET("/privacy-policy", privacyPolicy)

	server := http.Server{
		Addr:    ":9080",
		Handler: routes,
	}

	fmt.Println("Web server running...")
	server.ListenAndServe()
}
