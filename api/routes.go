package api

import (
	"github.com/labstack/echo"
	"bitbucket.org/boichai/boichai-frontend/templates"
	"html/template"
	"github.com/labstack/gommon/log"
	"os"
	"github.com/labstack/echo/middleware"
)

/**
 * := Coded with love by Sakib Sami on 25/5/18.
 * := root@sakib.ninja
 * := www.sakib.ninja
 * := Coffee : Dream : Code
 */

var e *echo.Echo

func InitWebServer() {
	e = echo.New()
	e.Logger.SetLevel(log.INFO)
	e.Logger.SetOutput(os.Stdout)
	e.Use(middleware.Logger())
	e.Use(middleware.StaticWithConfig(middleware.StaticConfig{
		Root:   "templates/public/",
		Browse: false,
	}))

	e.Renderer = &templates.BoiChaiTemplateRenderer{
		Templates: template.Must(template.ParseGlob("templates/views/*.html")),
	}

	setupFrontendRoutes()
	e.Logger.Fatal(e.Start(":9092"))
}

func setupFrontendRoutes() {
	e.GET("/", index)
	e.GET("/index", index)
	e.GET("/contact", contact)
}
