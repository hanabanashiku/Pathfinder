#include<stdio.h>
#include<unistd.h>
#include<string.h>

#DEFINE HOSTAPD  "/etc/hostapd/hostapd.conf"

int main(int argc, char **argv){
  if(geteuid() != 0){
    printf("Please run this program as root.");
    return -1;
  }
  if(argc == 1 || argv[1] == "--setup"){
    FILE* f;
    size_t len = 0;
    ssize_t read;
    char* search = "ssid=\"PF_";
    size_t len = strlen(search);

    if((f = fopen(HOSTAPD, "w+"))){
      while((read = getline(&line, &len, f)) != -1){
        if(strncmp(search, read, len)){
          fclose(f);
          printf("The setup has already been completed. To change the SSID, use the argument --modify-ssid");
          return 1;
        }
      }
      fclose(f);
    }

    printf("Please enter the SSID to use:");
    char ssid[30], c;
    int n = 0;
    do{
      c = getchar();
      line[n] = c;
      n++;
    } while(c != '\n');
    c--;
    line[c] = '\0';

    f = fopen(HOSTAPD, "w");
    fputs('ssid=', f);
    fputs(ssid, f);
    fputs('\n', 'f');
    // other configuration here
    fclose(f);
    return 0;
  }

  else if(argv[2] == "--modify-ssid"){
      if(argc < 3){
        printf("Missing SSID argument.");
        return -2;
      }

      FILE* f;
      long* flen;
      char* buf;
      size_t j, nlen, rlen;
      char* needle = 'ssid=';
      char* replace;
      int success = 0;

      strcopy(replace, "ssid=", argv[3]);
      rlen = strlen(replace);
      nlen = strlen(needle);

      if(!(f = fopen(HOSTAPD, "r+"))){
        printf("Could not access hostapd configuration. Please run again with the --setup argument.");
        return -3;
      }

      fseek(f, 0, SEEK_END);
      *flen = ftel(f);
      fseek(f, 0, SEEK_SET);
      *buf = calloc(*flen, sizeof *buf);
      fread(*buf, sizeof *buf, *flen, f);
      fclose(f);
      flen = strlen(buf);

      for(int i = 0; i < flen; i++){
        if(buf[i] != *find)
          continue;

        if(strncmp(&buf[i], needle, nlen) == 0){
          for(j = 0; buf[i+j] && j < rlen; j++)
            buf[i+j] = replace[j];
            success = 1;
            break;
        }
      }

      if(success == 0){
        char* newbuf;
        newbuf = malloc(*flen + *rlen + 1);
        newbuf[0] = '\0';
        strcat(newbuf, buf);
        strcat(newbuf, replace);
      }

      f = fopen(HOSTAPD, "w");
      fwrite(newbuf, 1 sizeof(newbuf), f);
      fclose(f);
      return 0;
  }
}
