package templates

import (
	"html/template"
	"io"
	"github.com/labstack/echo"
)

/**
 * := Coded with love by Sakib Sami on 25/5/18.
 * := root@sakib.ninja
 * := www.sakib.ninja
 * := Coffee : Dream : Code
 */

type BoiChaiTemplateRenderer struct {
	Templates *template.Template
}

func (t *BoiChaiTemplateRenderer) Render(w io.Writer, name string, data interface{}, c echo.Context) error {
	return t.Templates.ExecuteTemplate(w, name, data)
}
