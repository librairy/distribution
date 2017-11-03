Packaged installs: Ensure that the following settings are included in the /etc/security/limits.d/cassandra.conf file:
cassandra - memlock unlimited
cassandra - nofile 100000
cassandra - nproc 32768
cassandra - as unlimited
Tarball installs: Ensure that the following settings are included in the /etc/security/limits.conf file:
* - memlock unlimited
* - nofile 100000
* - nproc 32768
* - as unlimited
If you run Cassandra as root, some Linux distributions such as Ubuntu, require setting the limits for root explicitly instead of using *:
root - memlock unlimited
root - nofile 100000
root - nproc 32768
root - as unlimited
For CentOS, RHEL, OEL systems, also set the nproc limits in /etc/security/limits.d/90-nproc.conf :
* - nproc 32768

For installations on Debian and Ubuntu operating systems, the pam_limits.so module is not enabled by default. Edit the /etc/pam.d/su file and uncomment this line:
session    required   pam_limits.so

For all installations, add the following line to /etc/sysctl.conf :



# Disable Ipv6
#net.ipv6.conf.all.disable_ipv6 = 1
#net.ipv6.conf.default.disable_ipv6 = 1
#net.ipv6.conf.lo.disable_ipv6 = 1

fs.file-max = 100000

# Swappiness
vm.swappiness=1

vm.max_map_count = 1048575

To make the changes take effect, reboot the server or run the following command:
$ sudo sysctl -p
To confirm the limits are applied to the Cassandra process, run the following command where pid is the process ID of the currently running Cassandra process:
$ cat /proc/<pid>/limits


# Important Caveats
• Data volumes are required for the commitlog, saved_caches, and data directories (everything in /var/lib/cassandra). The data volume must use a supported file system (usually xfs or ext4).
• Docker’s default networking (via Linux bridge) is not recommended for the production use as it slows down networking considerably. Instead, use the host networking (docker run --net=host) or a plugin that can manage IP ranges across clusters of hosts. The host networking limits the number of DSE nodes per a Docker host to one, but this is the recommended configuration to use in production.
```
network_mode: host
```
• Development and testing benefit from running DSE clusters on a single Docker host and for such scenarios the default networking is just fine.
• Before running DSE within Docker (especially in production), it’s prudent that various configuration options are adjusted for the Docker environment (for e.g. fine tuning of dse.yaml, cassandra.yaml, turning swap off on the Docker host, choosing where to manage Cassandra data, calculating optimal JVM heap size, choosing optimal garbage collector, etc).
• The default capability limits of Docker containers break mlockall functionality that Cassandra uses to prevent swapping and page faults. The simplest workaround is to add -XX:+AlwaysPreTouch to the JVM arguments and disable swap on the host OS.
• All containers by default inherit ulimits from the Docker daemon. DSE containers should have them set to unlimited or reasonably high values (for e.g. for mem_locked_memory and max_memory_size).
• There is no support for rack / data center placement for Dockerized nodes. To enable this capability, please refer to this example.
• Token ranges need to be calculated post run (by exposing a node’s configuration via mounted volumes).
• Whenever a node starts up it will place its IP address within the /data/ip.address. This can be used as a set of SEEDS to spawn nodes at a later time.
• Certain global state is kept within Cassandra tables (e.g. system.peers, cluster name, etc.). It is not advisable to change any of these whenever a new container is started which re-uses existing data files.