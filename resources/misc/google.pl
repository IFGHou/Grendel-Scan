use strict;
my (@list, $word, %words, $fh);
getList();

open($fh, ">results3");

for $word(@list)
{
	my $count = 0;
	my $text = `curl --user-agent "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.13) Gecko/20080311 Firefox/2.0.0.13" http://www.google.com/search?q=filetype\%3A$word`;
	if ($text =~ /<\/b> of about <b>([0-9,]+)/i)
	{
		$count = $1;
		$count =~ s/,//g;
		$words{$word} += $count;	
	}
	print "tested $word - $count\n\n";
	print $fh "tested $word - $count\n\n";
	sleep(15);
}

print "\n\n\n\n";
for $word (sort {$words{$b} <=> $words{$a}} keys(%words))
{
	print "$word\t$words{$word}\n";
}

sub getList
{
	@list = qw(
asa
asax
ashx
asmx
asp
aspx
axd
backup
bak
bakup
bat
cfc
cfm
cgi
com
dll
do
doc
exe
gz
hta
htm
html
htr
inc
jhtm
jhtml
jsa
jsp
jws
log
mdb
mht
mhtml
mv
nsf
old
php
php3
phtml
pl
plx
rar
reg
sav
saved
sh
shtm
shtml
sql
stm
swf
tar
tar.gz
tgz
tmp
txt
vbs
wsdl
xdl
xhtml
xml 
zip
);
}