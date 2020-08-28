FROM mcr.microsoft.com/vscode/devcontainers/universal:linux

# [Optional] Install Docker
USER root
ARG INSTALL_DOCKER="false"
COPY library-scripts/docker-debian.sh /tmp/library-scripts/
RUN if [ "${INSTALL_DOCKER}" = "true" ]; then \
        bash /tmp/library-scripts/docker-debian.sh "true" "/var/run/docker-host.sock" "/var/run/docker.sock" "codespace"; \
    else \
        echo '#!/bin/bash\nexec "$@"' > /usr/local/share/docker-init.sh && chmod +x /usr/local/share/docker-init.sh; \
    fi \
    && rm -rf /var/lib/apt/lists/* /tmp/library-scripts/

# Set the user
USER user5k

# Update apt
RUN sudo apt update -y 

# Install needed packages
RUN sudo apt install -y python3 python3-pip

# Install sdkman
RUN curl -s "https://get.sdkman.io?rcupdate=true" | bash
RUN source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install gradle
RUN sdk install gradle