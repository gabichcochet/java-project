package main

import (
	"fmt"
	"strings"
)

func compareStrings(str1, str2 string) string {
	accents := map[rune]rune{
		'à': 'a', 'á': 'a', 'ä': 'a', 'â': 'a', 'ã': 'a', 'å': 'a',
		'è': 'e', 'é': 'e', 'ë': 'e', 'ê': 'e',
		'ì': 'i', 'í': 'i', 'ï': 'i', 'î': 'i',
		'ò': 'o', 'ó': 'o', 'ö': 'o', 'ô': 'o', 'õ': 'o',
		'ù': 'u', 'ú': 'u', 'ü': 'u', 'û': 'u',
		'ç': 'c',
		'ñ': 'n',
	}
	removeAccentsAndLower := func(inputStr string) string {
		inputStr = strings.ToLower(inputStr)

		var result []rune
		for _, r := range inputStr {
			if replacement, found := accents[r]; found {
				result = append(result, replacement)
			} else {
				result = append(result, r)
			}
		}
		return string(result)
	}
	str1 = removeAccentsAndLower(str1)
	str2 = removeAccentsAndLower(str2)
	if strings.Contains(str1, str2) {
		return str2
	}
	return "''''"
}

func main() {
	result := compareStrings("Élève à l'école","haha")
	fmt.Println(result) 
}
