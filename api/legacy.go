package api

import (
	"net/http"
	"github.com/gin-gonic/gin"
)

/**
 * := Coded with love by Sakib Sami on 4/8/18.
 * := root@sakib.ninja
 * := www.sakib.ninja
 * := Coffee : Dream : Code
 */

func index(c *gin.Context) {
	c.HTML(http.StatusOK, "index", gin.H{
		"home": "active",
	})
}

func contact(c *gin.Context) {
	c.HTML(http.StatusOK, "contact", gin.H{
		"contact": "active",
	})
}

func privacyPolicy(c *gin.Context) {
	c.HTML(http.StatusOK, "privacy_policy", gin.H{
		"privacy": "active",
	})
}
