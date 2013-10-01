use strict;
my (@list, $word, %words, $fh);
getList();


for $word(@list)
{
	my $count = 0;
	my $text = `curl --user-agent "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13" http://search.yahoo.com/search?p=inurl%3A$word`;
	if ($text =~ /\d+ - \d+ of (?:about )?([0-9,]+) for <strong>/i)
	{
		$count = $1;
		$count =~ s/,//g;
		$words{$word} += $count;	
	}
	print "tested $word - $count\n\n";
}

open($fh, ">yahoo_file_results.xls");
for $word (sort {$words{$b} <=> $words{$a}} keys(%words))
{
	print $fh "$word\t$words{$word}\n";
}
close($fh);

sub getList
{
	@list = qw(
	adm
admin
administrator
adminlogon
admin_
admin_login
admin_logon
backend
backup
bin
client
clients
cmd
customer
data
database
default
details
download
email
example
examples
feedback
global
globals
guestbook
index
INSTALL_admin
left
log
logfile
logfiles
login
logon
logs
mail
main
members
pass
passwd
password
passwords
perl
query
readme
right
robots
root
send
sensepost
settings
shell
sign
signin
sitemap
source
src
staff
stats
test
trace
update
upload
uploader
uploads
user
users
web
website
www
);
}