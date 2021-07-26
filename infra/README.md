# Infra
This project includes the necessary scripts and configuration files to deploy the applications to cloud servers (like a DigitalOcean Droplet, or an Amazon EC2 instance).

## Requirements
The scripts work with [Ansible](https://www.ansible.com/) `2.9.23`, it is likely that other versions would work too.

There [test-hosts.ini](./test-hosts.ini) inventory file is an example configuration that is used to deploy the demo apps, it includes the necessary comments for adapting it to your own environment.

A postgres database is required, you can use either a managed database or set up a local one by following these [instructions](./setup-postgres.md).

Modify the [server](./config/server/dev.env.j2) configuration that are required while deploying it.

The scripts are tested in Ubuntu 20.04 with paswordless sudo (meaning that `sudo ls` works without a password), they likely works in other Ubuntu based operating systems.


## Playbooks
There are many playbooks involved to let you deploy the necessary pieces only:
- [server.yml](./server.yml) deploys the [server](../server) application to the cloud server.
- [web.yml](./web.yml) deploys the [web](../web) application to the cloud server.
- [admin.yml](./admin.yml) deploys the [admin](../admin) application to the cloud server.
- [nginx.yml](./nginx.yml) installs nginx in the cloud server, which is used to serve the requests from the public internet.
- [nginx_site_admin.yml](./nginx_site_admin.yml) exposes the [admin](../admin) application to the internet, also, it gets and configures a SSL certificate to access it using https, to run this, nginx should be already deployed, also, a domain should be linked to your cloud server.
- [nginx_site_web.yml](./nginx_site_web.yml) exposes the [web](../web) application to the internet, also, it gets and configures a SSL certificate to access it using https, to run this, nginx should be already deployed, also, a domain should be linked to your cloud server.

**NOTE** You will likely run the nginx stuff only once.

After setting up everything:
1. Deploy nginx: `ansible-playbook -i test-hosts.ini nginx.yml`
2. Deploy the apps with: `ansible-playbook -i test-hosts.ini server.yml web.yml admin.yml`
3. Expose the apps to the internet with: `ansible-playbook -i test-hosts.ini nginx_site_admin.yml nginx_site_web.yml`

Once everything is ready, run the first step to deploy the apps again (or use a single playbook to deploy a single app instead).
