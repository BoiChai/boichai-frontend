package api

import (
	"github.com/labstack/echo"
	"net/http"
)

/**
 * := Coded with love by Sakib Sami on 4/8/18.
 * := root@sakib.ninja
 * := www.sakib.ninja
 * := Coffee : Dream : Code
 */

func index(c echo.Context) error {
	return c.Render(http.StatusOK, "index.html", echo.Map{})
}

func contact(c echo.Context) error {
	return c.Render(http.StatusOK, "contact.html", echo.Map{})
}

func privacyPolicy(c echo.Context) error {
	return c.Render(http.StatusOK, "index.html", echo.Map{})
}

func faq(c echo.Context) error {
	return c.Render(http.StatusOK, "index.html", echo.Map{})
}
