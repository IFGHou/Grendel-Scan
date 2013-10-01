use strict;
my($fh, $text, %words);
$text = `cat 5k`;
while ($text =~ /([a-z]+)/igs)
{
	my $word = lc($1);
	next if length($word) <= 3;
	my $skip = 0;
	for my $char('a'..'z')
	{
		my $seq = $char x 3;
		if (index($word, $seq) >= 0)
		{
			$skip = 1;
			last;
		}
	}
	next if $skip;
	$words{$word}++;
}

for my $word (sort keys(%words))
{
	print "$word\n";
}