#include <stdio.h>


int main(int argc, char *argv[], char *envp[])
{
	int i = 0;
	
	printf(
		"<html xml:lang=\"en\" lang=\"en\">\r\n"
		"<head>\r\n"
		"<meta charset=\"UTF-8\">\r\n"
		"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
		"<title>Document</title>\r\n"
		"</head>\r\n"
		"<body>\r\n"
	);
	while (envp[i]){
		printf("<p>%s</p>\n", envp[i++]);
	}
	printf("</body>\r\n"
			"</html>\r\n");
}
