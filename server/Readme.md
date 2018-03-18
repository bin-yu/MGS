1. How to install mgs-server as Linux service:
1) ln -s /usr/local/mgs/mgs.jar /etc/init.d/mgs
2) vi mgs.conf
	JAVA_HOME=/usr/local/java/jdk1.8.0_152
	JAVA_OPTS="-Dspring.profiles.active=alpha -Djava.security.egd=file:/dev/./urandom"
3) update-rc.d mgs defaults 99
4) service mgs start
5) systemctl status mgs.service

2. file permissions to set:
root@iZwz9g85umiihemljh5b8sZ:/usr/local/mgs# ls -l
total 47456
-rw-r--r-- 1 root root      111 Mar 15 14:02 application.properties
drwxr-xr-x 3 root root     4096 Mar 17 22:17 db
-r-------- 1 root root       84 Mar 17 21:00 mgs.conf
-r-x------ 1 root root 39464415 Mar 17 20:35 mgs.jar
-rw-r--r-- 1 root root  7618204 Mar 17 22:21 mgs.log
-rw-r--r-- 1 root root  1494570 Mar 17 20:33 mgs.out