import os


def git_pull():
    log_current_command("git_pull")
    user = os.getenv("GIT_USERNAME")
    token = os.getenv("GIT_TOKEN")
    command = f"git pull https://{user}:{token}@github.com/Abilmuzhubayev/dormitory_marketplace"
    os.system(command)


def build_jar():
    log_current_command("build_jar")
    mvn_clean_command = "mvn clean"
    mvn_package_command = "mvn -DskipTests=true package"
    os.system(mvn_clean_command)
    os.system(mvn_package_command)


def docker_compose_up():
    log_current_command("docker-compose up")
    up_command = "sudo docker-compose up --build"
    os.system(up_command)


def docker_compose_down():
    log_current_command("docker-compose down")
    down_command = "sudo docker-compose down"
    os.system(down_command)


def log_current_command(command):
    print(f"Script is executing: {command}")
    print("---------------------")


docker_compose_down()
git_pull()
build_jar()
docker_compose_up()